package com.example.breezy.screen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.breezy.MainActivity
import com.example.breezy.WeatherServiceTest
import com.example.breezy.ZipServiceTest
import com.example.breezy.screens.DailyWeatherScreen
import com.example.breezy.serialobjects.Coord
import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.ZipCoords
import com.example.breezy.viewmodels.LocationViewModel
import com.example.breezy.viewmodels.WeatherViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DailyWeatherScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val weatherService = WeatherServiceTest()
    private val zipService = ZipServiceTest()
    private val dispatcher = UnconfinedTestDispatcher()
    private val locationViewModel = LocationViewModel(
        dispatcher = dispatcher
    )

    @Test
    fun testScreenAccept(){
        weatherService.weatherResponse = CurrentWeather(Coord(5.5,6.5))
        zipService.zipServiceResponse = ZipCoords(55331,"Test",6.5,5.5,"US")

        val weatherViewModel = WeatherViewModel(
            weatherService = weatherService,
            apiKey = "",
            zipService = zipService,
            dispatcher = dispatcher
        )
        composeTestRule.setContent{
            DailyWeatherScreen(
                weatherViewModel = weatherViewModel,
                locationViewModel = locationViewModel,
                onForecastClicked = { }
            )
        }
    }

    @Test
    fun testScreenFails(){
        weatherService.shouldFail = true
        zipService.shouldFailZip = true
        
        val weatherViewModel = WeatherViewModel(
            weatherService = weatherService,
            apiKey = "",
            zipService = zipService,
            dispatcher = dispatcher
        )
        composeTestRule.setContent{
            DailyWeatherScreen(
                weatherViewModel = weatherViewModel,
                locationViewModel = locationViewModel,
                onForecastClicked = { }
            )
        }
    }
}