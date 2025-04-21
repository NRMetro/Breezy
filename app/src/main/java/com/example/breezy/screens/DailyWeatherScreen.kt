package com.example.breezy.screens
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.services.LocationService
import com.example.breezy.viewmodels.LocationViewModel
import com.example.breezy.R
import com.example.breezy.viewmodels.WeatherViewModel

val openSans = FontFamily(Font(R.font.open_sans))

@SuppressLint("ContextCastToActivity")
@Composable
fun DailyWeatherScreen(
    weatherViewModel: WeatherViewModel,
    locationViewModel: LocationViewModel,
    onForecastClicked: () -> Unit
) {
    val currentWeather by weatherViewModel.weather.collectAsState()
    val zipCoords by weatherViewModel.coords.collectAsState()
    val sharedPreferences = LocalContext.current.getSharedPreferences("BreezyPrefs",Context.MODE_PRIVATE)
    val errorMessage by weatherViewModel.errorMessage.collectAsState()
    val locService by locationViewModel.location.collectAsState()
    val context = LocalContext.current
    var needNotification by remember { mutableStateOf(false) }

    LaunchedEffect(zipCoords) {
        weatherViewModel.fetchWeather(
            latitude = zipCoords.lat,
            longitude = zipCoords.lon
        )
        if(checkNotificationPerm(context)){
            needNotification = true
        }

    }

    LaunchedEffect(locService) {
        locService?.let{
            val lat = locService!!.latitude
            val long = locService!!.longitude
            Log.d("ABC","$lat and $long")
            weatherViewModel.fetchWeather(
                latitude = lat,
                longitude = long
            )

            if(checkNotificationPerm(context)){
                needNotification = true
            }
        }
    }

    val zipCodeEntered: (Int) -> Unit = { item ->
        weatherViewModel.fetchCoords(item)
    }
    val defaultClicked: () -> Unit = {
        val editor = sharedPreferences.edit()
        editor.putFloat("lon", zipCoords.lon.toFloat())
        editor.putFloat("lat", zipCoords.lat.toFloat())
        editor.apply()
    }

    val window = (LocalContext.current as Activity).window
    window.statusBarColor = Color(0xFF4448d8).toArgb()
    window.navigationBarColor = Color(0xFF4448d8).toArgb()


    Column(
        modifier = Modifier
            .background(color = Color(0xFF4448d8))
            .fillMaxHeight()
    ){
        errorMessage?.let {
            Snackbar(
                dismissAction = {
                    Text(
                        "OK",
                        modifier = Modifier
                            .clickable { weatherViewModel.errorShown() }
                            .testTag("ErrorShown")
                    )
                }
            ) {
                Text(it)
            }
        }

        if(currentWeather.name == "" && sharedPreferences.getFloat("lat",-1f).toDouble() != -1.0){
            weatherViewModel.fetchWeather(latitude = sharedPreferences.getFloat("lat",-1f).toDouble(),
                longitude = sharedPreferences.getFloat("lon",-1f).toDouble())
        }

        AppHeader(currentWeather,zipCodeEntered,defaultClicked)



        Row(
            modifier = Modifier.fillMaxWidth(),
            //horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        end = 30.dp
                    )
            ) {

                Row(){
                    LargeTemp(currentWeather)
                    Column(
                        modifier = Modifier.padding(top = 50.dp)
                    ) {

                        Image(
                            painter = painterResource(weatherViewModel.weatherIcon()),
                            contentDescription = "weatherType"
                        )
                    }
                }
                HighLow(currentWeather)
                Stats(currentWeather)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            ForecastButton(onForecastClicked)
        }
    }
}

@Composable
fun ZipCode(zipClicked: (Int) -> Unit,defaultClicked:() -> Unit) {
    var zip by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    errorMessage?.let {
        Snackbar(
            dismissAction = {
                Text(
                    "OK",
                    modifier = Modifier.clickable { errorMessage = null }
                )
            }
        ) {
            Text("Improper Zip $it")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        TextField(
            value = zip,
            onValueChange = {
                zip = it
            },
            label = {Text("Change Zipcode")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Column {
            Button(
                onClick = {
                    if(!zip.contains("[^0-9]".toRegex()) && zip.length == 5){
                        zipClicked(zip.toInt())
                    }
                    else{
                        errorMessage = zip
                    }
                }
            ){
                Text("Enter")
            }
        }

    }
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = defaultClicked
        ) {
            Text("Change Default City")
        }
    }

}

@Composable
fun AppHeader(
    currentWeather: CurrentWeather,
    zipClicked: (Int) -> Unit,
    defaultClicked: () -> Unit
){
    Row(
        Modifier
            .padding(bottom = 20.dp, top = 20.dp, start = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Location Icon",
            tint = Color.White
        )

        Text(
            text = currentWeather.name,
            fontSize = 25.sp,
            color = Color.White,
            style = TextStyle(
                fontFamily = openSans,
            )
        )

        MyLocationButton()
        MenuButton(zipClicked,defaultClicked)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(zipClicked: (Int) -> Unit,defaultClicked:() -> Unit){
    var openMenu by remember { mutableStateOf<Boolean?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ){
        Button(
            onClick = {
                openMenu = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(255, 255, 255,0)
            ),
            modifier = Modifier
                .testTag("MenuButton")
        ) {
            Icon(
                imageVector =  Icons.Default.Menu,
                contentDescription = "Location Icon",
                tint = Color.White
            )
        }
    }


    openMenu?.let {
        ModalBottomSheet(
            onDismissRequest = { openMenu = null}
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                ZipCode(zipClicked = zipClicked,defaultClicked = defaultClicked)
            }
        }
    }
}

@Composable
fun ForecastButton(onForecastClicked: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxHeight(),
        verticalAlignment = Alignment.Bottom
    ){
        Button(
            onClick = onForecastClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF695dec)),
            modifier = Modifier
                .testTag("ForecastButton")
        ) {
            Text("Check Forecast")
        }
    }
}

fun checkNotificationPerm(
    context: Context
): Boolean {
    var permissionsCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
    if(permissionsCheckResult == PackageManager.PERMISSION_GRANTED){
        permissionsCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE)
        if(permissionsCheckResult == PackageManager.PERMISSION_GRANTED){
            return true
        }
    }
    return false
}

fun checkLocationPermission(
    context: Context
): Boolean {
    val permissionsCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    if(permissionsCheckResult == PackageManager.PERMISSION_GRANTED){
        return true
    }
    else return false
}

@Composable
fun MyLocationButton(){
    val context = LocalContext.current

    val startLocationService = {
        val intent = Intent(context, LocationService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    var pendingAction by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingAction) {
            startLocationService()
            pendingAction = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (checkNotificationPerm(context)) {
                startLocationService()
            } else {
                pendingAction = true
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Button(
        onClick = {
            if (checkLocationPermission(context)) {
                if (checkNotificationPerm(context)) {
                    startLocationService()
                } else {
                    pendingAction = true
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(255, 255, 255,0)
        ),
        shape = CircleShape,
        modifier = Modifier
            .size(60.dp)
            .testTag("LocationButton"),
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.my_location_24px) ,
            contentDescription = "My Location"
        )
    }
}

@Composable
fun LargeTemp(currentWeather: CurrentWeather){
    val context = LocalContext.current
    val tempVal = currentWeather.main.temp.toInt().toString()
    val tempText = context.getString(R.string.temp)

    Row(
    ){
        Text(
            text = tempVal,
            fontSize = 160.sp,
            fontFamily = openSans,
            modifier = Modifier.align(Alignment.Top),
            style = TextStyle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White,Color.White,Color.DarkGray)
                )
            )
        )
        Text(
            text = tempText,
            fontSize = 60.sp,
            modifier = Modifier
                .align(Alignment.Top)
                .offset(x = ((-10).dp)),
            style = TextStyle(
                fontFamily = openSans,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White,Color.White,Color.DarkGray)
                ),
                baselineShift = BaselineShift(-.5f)
            )
        )
    }

}

@Composable
fun HighLow(currentWeather: CurrentWeather){
    val context = LocalContext.current
    val low = context.getString(R.string.low,currentWeather.main.tempMin.toInt())
    val loNHigh = context.getString(R.string.high,currentWeather.main.tempMax.toInt()) + " " + low
    val openSans = FontFamily(Font(R.font.open_sans))
    Box(
        modifier = Modifier
            .size(140.dp,40.dp)
            .clip(CircleShape)
            .border(width = 1.dp,color = Color.LightGray,shape = CircleShape)
            .background(color = Color(0xFF695dec)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = loNHigh,
            fontSize = 18.sp,
            style = TextStyle(fontFamily = openSans),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
fun Stats(currentWeather: CurrentWeather){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    color = Color(0xFF695dec)
                )
                .border(width = 1.dp,color = Color.LightGray,shape = RoundedCornerShape(30.dp))

        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.feels_like, currentWeather.main.feelsLike.toInt()),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .height(75.dp)
                    .width(1.dp)
                    .background(Color.White)
                    .align(Alignment.CenterVertically)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.humidity, currentWeather.main.humidity) + "%",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .height(75.dp)
                    .width(1.dp)
                    .background(Color.White)
                    .align(Alignment.CenterVertically)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.pressure,currentWeather.main.pressure),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
    }

}


