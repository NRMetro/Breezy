package com.example.breezy

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
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
    private var bound = false
    private var service: LocationService? = null
    private lateinit var locationViewModel: LocationViewModel

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as LocationService.LocalBinder
            service = localBinder.getService()
            bound = true

            service?.setOnLocationUpdateListener { location ->
                locationViewModel.updateLocation(location)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val weatherService = createRetrofitService()
        val zipService = createRetrofitServiceZip()

        val apiKey = resources.getString(R.string.weather_api_key)
        val weatherViewModel = WeatherViewModel( weatherService,apiKey,zipService)

        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        setContent {
            BreezyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box (
                        modifier = Modifier.padding(innerPadding)
                    ){
                        WeatherNavigation(weatherViewModel)
                    }

                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocationService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(connection)
            bound = false
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
                    weatherViewModel = weatherViewModel,
                    locationViewModel = locationViewModel,
                    onForecastClicked = { navController.navigate(ForecastDestination) }
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