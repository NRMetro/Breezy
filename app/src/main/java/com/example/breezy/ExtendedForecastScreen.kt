package com.example.breezy

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExtendedForecastScreen(
    viewModel: WeatherViewModel,
    onBackClicked: () -> Unit
){
    val forecast by viewModel.forecast.observeAsState()
    val zipCoords by viewModel.coords.observeAsState()
    val sharedPreferences = LocalContext.current.getSharedPreferences("BreezyPrefs", Context.MODE_PRIVATE)


    if(zipCoords != null){
        zipCoords?.let { coords ->
            viewModel.fetchForecast(
                latitude = coords.lat,
                longitude = coords.lon
            )
        }
    }
    else if(forecast == null){
        viewModel.fetchForecast(latitude = sharedPreferences.getFloat("lat",-1f).toDouble(),
            longitude = sharedPreferences.getFloat("lon",-1f).toDouble())
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(35.dp)
    ) {
        Button(
            onClick = onBackClicked,
        ) {
            Text("Return")
        }
        Text(
            text = "Next 14 Days",
            fontSize = 26.sp
        )

        forecast?.let{ forecast ->
            ForecastScreen(forecast.list,viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastScreen(forecastItems: List<WeatherList>,viewModel: WeatherViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(10.dp)
    ) {
        items(forecastItems){ forecastItem ->
            ForecastItemView(forecastItem,viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastItemView(weather: WeatherList,viewModel: WeatherViewModel) {
    val context = LocalContext.current
    Row(
        Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        val dt = Instant.ofEpochSecond(weather.dt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        Box(
            modifier = Modifier
                .size(50.dp)
        ) {

            Image(
                painter = painterResource(viewModel.weatherIcon(weather.weather[0].main)),
                contentDescription = "weatherType"
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp)
        ) {
            Text(
                text = context.getString(
                    R.string.forecastingDate,
                    dt.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    dt.dayOfMonth,
                    dt.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                )
            )

            var string = ""
            if(weather.rain != 0.0){
                string = context.getString(R.string.forecastPrecip, "Rain",weather.rain) + "mm"
            }
            else if(weather.snow != 0.0){
                string = context.getString(R.string.forecastPrecip, "Snow",weather.snow) + "mm"
            }
            else{
                string = context.getString(R.string.forecastPrecip, "Clouds",weather.clouds.toDouble()) + "%"
            }
            Text(
                text = string
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = context.getString(R.string.high, weather.temp.max.toInt())
                        + context.getString(R.string.low, weather.temp.min.toInt())
            )
        }
    }

    Box(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.DarkGray)
    )
}