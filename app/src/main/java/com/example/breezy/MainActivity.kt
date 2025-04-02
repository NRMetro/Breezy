package com.example.breezy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.breezy.ui.theme.BreezyTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Serializable
object DailyWeather

@Serializable
object ForecastDestination

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val weatherService = createRetrofitService()
        val zipService = createRetrofitServiceZip()

        val apiKey = resources.getString(R.string.weather_api_key)
        val viewModel = WeatherViewModel( weatherService,apiKey,zipService)

        setContent {
            BreezyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        WeatherNavigation(viewModel)
                    }

                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherNavigation(
    weatherViewModel: WeatherViewModel
){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = DailyWeather){
        composable<DailyWeather>{
            DailyWeatherScreen(
                viewModel = weatherViewModel,
                onForecastClicked = {navController.navigate(ForecastDestination)}
            )
        }

        composable<ForecastDestination> {
            ExtendedForecastScreen(
                viewModel = weatherViewModel,
                onBackClicked = { navController.popBackStack() }
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

fun createRetrofitServiceZip(): ZipService {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/geo/1.0/")
        .client(client)
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json".toMediaType()
            ))
        .build()
        .create(ZipService::class.java)
}

//@Preview(showBackground = true)
//@Composable
//fun FrontPreview(){
//    BreezyTheme {
//        WeatherScreen()
//    }
//}