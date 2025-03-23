package com.example.breezy

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DailyWeatherScreen(
    viewModel: WeatherViewModel
) {

    val currentWeather by viewModel.weather.observeAsState()
    val zipCoords by viewModel.coords.observeAsState()

    LaunchedEffect(zipCoords) {
        zipCoords?.let { coords ->
            viewModel.fetchWeather(
                    latitude = coords.lat,
                    longitude = coords.lon
            )
        }
    }

    Column(){
        val zipCodeEntered: (Int) -> Unit = { item ->
            viewModel.fetchCoords(item)
        }

        AppHeader()
        ZipCode(zipCodeEntered)

        currentWeather?.let { CityName(it) }
        currentWeather?.let { Stats(it) }

    }

}

@Composable
fun ZipCode(onZipClicked: (Int) -> Unit) {

    var zip by remember { mutableStateOf("") }
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
        Button(
            onClick = {
                onZipClicked(zip.toInt())
            }
        ){

        }
    }
}

@Composable
fun AppHeader(){
    val context = LocalContext.current

    Row(
        Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(6.dp)
    ){
        Text(
            text = context.getString(R.string.app_name)
        )
    }
}

@Composable
fun CityName(currentWeather: CurrentWeather){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = currentWeather.name
        )
    }
}

@Composable
fun WeatherIcon(){
    Column{
        val image = painterResource(R.drawable.sun)
        Image(
            painter = image,
            contentDescription = "Sunny",
            modifier = Modifier
                .fillMaxWidth(.36f)
                .padding(top = 14.dp)
        )

    }
}

@Composable
fun Stats(currentWeather: CurrentWeather){
    val context = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(.5f)
        ){
            Column (
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 18.dp)

            ){
                val tempVal = currentWeather.main.temp.toInt()
                val tempText = tempVal.toString() + context.getString(R.string.temp)
                Text(
                    text = tempText,
                    fontSize = 60.sp
                )
                val feelsLikeVal = currentWeather.main.feelsLike.toInt()
                val feelsLikeText = context.getString(R.string.feels_like) + feelsLikeVal + context.getString(R.string.temp)
                Text(
                    text = feelsLikeText,
                    modifier = Modifier.padding(start = 6.dp,top = 4.dp)
                )
            }
            val lowVal = currentWeather.main.tempMin.toInt()
            val low = context.getString(R.string.low) + lowVal + context.getString(R.string.temp)

            val highVal = currentWeather.main.tempMax.toInt()
            val high = context.getString(R.string.high) + highVal + context.getString(R.string.temp)

            val humidityVal = currentWeather.main.humidity
            val humidity = context.getString(R.string.humidity) + humidityVal + context.getString(R.string.percent)

            val pressureVal = currentWeather.main.pressure
            val pressure = context.getString(R.string.pressure) + pressureVal + context.getString(R.string.pressureUnit)
            Column{
                Text(low)
                Text(high)
                Text(humidity)
                Text(pressure)
            }
        }

        WeatherIcon()
    }

}
