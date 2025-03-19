package com.example.breezy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breezy.ui.theme.BreezyTheme
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val weatherService = createRetrofitService()
        val apiKey = resources.getString(R.string.weather_api_key)
        val latitude = resources.getString(R.string.latitude).toDouble()
        val longitude = resources.getString(R.string.longitude).toDouble()
        val viewModel = WeatherViewModel( weatherService,apiKey,latitude,longitude)
        viewModel.fetchWeather()
        setContent {
            BreezyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        WeatherScreen(viewModel = viewModel)
                    }

                }
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {

    val currentWeather by viewModel.weather.observeAsState()

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

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        currentWeather?.name?.let {
            Text(
                text = it
            )
        }
    }

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
                val tempVal = currentWeather?.main?.temp?.toInt()
                val tempText = tempVal.toString() + context.getString(R.string.temp)
                Text(
                    text = tempText,
                    fontSize = 60.sp
                )
                val feelsLikeVal = currentWeather?.main?.feelsLike?.toInt()
                val feelsLikeText = context.getString(R.string.feels_like) + feelsLikeVal + context.getString(R.string.temp)
                Text(
                    text = feelsLikeText,
                    modifier = Modifier.padding(start = 6.dp,top = 4.dp)
                )
            }
            val lowVal = currentWeather?.main?.tempMin?.toInt()
            val low = context.getString(R.string.low) + lowVal + context.getString(R.string.temp)

            val highVal = currentWeather?.main?.tempMax?.toInt()
            val high = context.getString(R.string.high) + highVal + context.getString(R.string.temp)

            val humidityVal = currentWeather?.main?.humidity
            val humidity = context.getString(R.string.humidity) + humidityVal + context.getString(R.string.percent)

            val pressureVal = currentWeather?.main?.pressure
            val pressure = context.getString(R.string.pressure) + pressureVal + context.getString(R.string.pressureUnit)
            Column{
                Text(low)
                Text(high)
                Text(humidity)
                Text(pressure)
            }
        }
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
}

fun createRetrofitService(): WeatherService {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .client(client)
        .addConverterFactory(
            Json.asConverterFactory(
            "application/json".toMediaType()
        ))
        .build()
        .create(WeatherService::class.java)
}

//@Preview(showBackground = true)
//@Composable
//fun FrontPreview(){
//    BreezyTheme {
//        WeatherScreen()
//    }
//}