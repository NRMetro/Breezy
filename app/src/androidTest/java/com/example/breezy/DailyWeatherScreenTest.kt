package com.example.breezy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.breezy.screens.DailyWeatherScreen
import com.example.breezy.serialobjects.Coord
import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.Main
import com.example.breezy.serialobjects.Weather
import com.example.breezy.serialobjects.ZipCoords
import com.example.breezy.viewmodels.LocationViewModel
import com.example.breezy.viewmodels.WeatherViewModel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test

class DailyWeatherScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val weatherService = WeatherServiceTest()
    private val zipService = ZipServiceTest()
    private val dispatcher = UnconfinedTestDispatcher()
    private val locationViewModel = LocationViewModel(
        dispatcher = dispatcher
    )

    @Test
    fun testScreenAccept(){
        val lat = 6.5
        val lon = 5.5
        val temp = 1.0
        val feelslike = 2.0
        val tempMin = 3.0
        val tempMax = 4.0
        val pressure = 500
        val humidity = 60
        weatherService.weatherResponse = CurrentWeather(
            coord = Coord(lon,lat),
            weather = listOf(Weather(1,"cloudy","desc","icon")),
            name = "TESTNAME",
            main = Main(temp,feelslike,tempMin,tempMax,pressure,humidity,70,80)
        )
        zipService.zipServiceResponse = ZipCoords(55331,"Test",lat,lon,"US")

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
            weatherViewModel.fetchCoords(55313)
            assert(zipService.actions.size  == 1)
            weatherViewModel.fetchWeather(5.5,6.5)
            assert(weatherService.actions.size  == 1)
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(temp.toInt().toString(), substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(feelslike.toInt().toString(), substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(tempMin.toInt().toString(), substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(tempMax.toInt().toString(), substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("" + humidity + "%", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("" + pressure + " hPa", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Check Forecast", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("TESTNAME", substring = true).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("MenuButton")
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag("LocationButton")
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag("ForecastButton")
            .assertIsDisplayed()
            .performClick()
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
            weatherViewModel.fetchCoords(55313)
            weatherViewModel.fetchWeather(5.5,6.5)
        }
    }
}