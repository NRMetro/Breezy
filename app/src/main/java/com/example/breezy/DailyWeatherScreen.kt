package com.example.breezy

import android.content.Context
import android.graphics.Color.rgb
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val openSans = FontFamily(Font(R.font.open_sans))

@Composable
fun DailyWeatherScreen(
    viewModel: WeatherViewModel,
    onForecastClicked: () -> Unit
) {
    val currentWeather by viewModel.weather.observeAsState()
    val zipCoords by viewModel.coords.observeAsState()
    val sharedPreferences = LocalContext.current.getSharedPreferences("BreezyPrefs",Context.MODE_PRIVATE)
    val errorMessage by viewModel.errorMessage


    LaunchedEffect(zipCoords) {
        zipCoords?.let { coords ->
            viewModel.fetchWeather(
                    latitude = coords.lat,
                    longitude = coords.lon
            )
        }
    }

    val zipCodeEntered: (Int) -> Unit = { item ->
        viewModel.fetchCoords(item)
    }
    val defaultClicked: () -> Unit = {
        val editor = sharedPreferences.edit()
        zipCoords?.let { coords ->
            editor.putFloat("lon", coords.lon.toFloat())
            editor.putFloat("lat", coords.lat.toFloat())
            editor.apply()
        }
    }

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
                        modifier = Modifier.clickable { viewModel.errorShown() }
                    )
                }
            ) {
                Text(it)
            }
        }

        currentWeather?.let { AppHeader(it) }
        ZipCode(zipCodeEntered,defaultClicked)

        if(currentWeather == null){
            viewModel.fetchWeather(latitude = sharedPreferences.getFloat("lat",-1f).toDouble(),
                longitude = sharedPreferences.getFloat("lon",-1f).toDouble())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            //horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .padding(
                        start = 40.dp
                    )
            ) {

                currentWeather?.let { LargeTemp(it) }
                currentWeather?.let { HighLow(it) }

            }
            //currentWeather?.let { Stats(it) }
            //WeatherIcon()
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
            label = {Text("Enter Zipcode")},
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
            Button(
                onClick = defaultClicked

            ) {
                Text("Change Default")
            }
        }
    }
}

@Composable
fun AppHeader(currentWeather: CurrentWeather){
    Row(
        Modifier
            .padding(bottom = 20.dp, top = 20.dp, start = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = currentWeather.name,
            fontSize = 25.sp,
            color = Color.White,
            style = TextStyle(
                fontFamily = openSans,
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ){
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255, 255, 255,0)
                )
            ) {
                Text(
                    text = "=",
                    fontSize = 40.sp
                )
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
            onClick = onForecastClicked
        ) {
            Text("Check Forecast")
        }
    }
}

@Composable
fun WeatherIcon(){
    Column(
        modifier = Modifier
            .fillMaxWidth(.4f)
            .padding(top = 6.dp)
    ){
        Row {
            val image = painterResource(R.drawable.sun)
            Image(
                painter = image,
                contentDescription = "Sunny"
            )
        }


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
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF695dec)),
        modifier = Modifier.size(140.dp,40.dp)
    ) {
        Text(
            text = loNHigh,
            fontSize = 18.sp,
            style = TextStyle(fontFamily = openSans)
        )
    }

}

@Composable
fun Stats(currentWeather: CurrentWeather){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth(.5f)
    ){
        Column (
            modifier = Modifier
                .padding(start = 12.dp, bottom = 18.dp)

        ){

            val feelsLikeText = context.getString(R.string.feels_like, currentWeather.main.feelsLike.toInt())
            Text(
                text = feelsLikeText,
                modifier = Modifier.padding(start = 6.dp,top = 4.dp)
            )
        }

        val humidity = context.getString(R.string.humidity, currentWeather.main.humidity)
        val pressure = context.getString(R.string.pressure,currentWeather.main.pressure)
        Column{

            Text(humidity)
            Text(pressure)
        }
    }


}
