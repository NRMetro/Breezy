package com.example.breezy

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext

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


    Column{
        Button(
            onClick = onBackClicked,
        ) {
            Text("Return")
        }
    }
}