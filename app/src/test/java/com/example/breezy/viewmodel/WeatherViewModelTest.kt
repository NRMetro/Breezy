package com.example.breezy.viewmodel

import androidx.compose.runtime.currentCompositionErrors
import com.example.breezy.serialobjects.City
import com.example.breezy.serialobjects.Coord
import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.Forecast
import com.example.breezy.serialobjects.ZipCoords
import com.example.breezy.service.WeatherServiceTest
import com.example.breezy.service.ZipServiceTest
import com.example.breezy.viewmodels.WeatherViewModel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherViewModelTest{
    private lateinit var weatherService: WeatherServiceTest
    private lateinit var zipService: ZipServiceTest
    private lateinit var viewModel: WeatherViewModel
    private lateinit var dispatcher: TestDispatcher

    @Before
    fun setup(){
        dispatcher = UnconfinedTestDispatcher()
        weatherService = WeatherServiceTest()
        zipService = ZipServiceTest()
        viewModel = WeatherViewModel(
            weatherService, "", zipService, dispatcher
        )
    }

    @Test
    fun `check coords and zipService pass and fail`() = runTest(dispatcher){
        val lat = 5.5
        val lon = 6.5
        val zipCoords = ZipCoords(55331,"Test",lat,lon,"US")
        zipService.zipServiceResponse = zipCoords
        viewModel.fetchCoords(55331)

        assertEquals(zipCoords, viewModel.coords.value)

        zipService.shouldFailZip = true
        viewModel.fetchCoords(55331)

        assertNotNull(viewModel.errorMessage.value)
    }

    @Test
    fun `check CurrentWeather pass and fail` () = runTest(dispatcher){
        val lat = 5.5
        val lon = 6.5
        val currentWeather = CurrentWeather(Coord(lon,lat))
        weatherService.weatherResponse = currentWeather
        viewModel.fetchWeather(lat,lon)

        assertEquals(currentWeather, viewModel.weather.value)

        weatherService.shouldFail = true
        viewModel.fetchWeather(lat,lon)

        assertNotNull(viewModel.errorMessage.value)
    }

    @Test
    fun `check forecast pass and fail` () = runTest(dispatcher){
        val lat = 5.5
        val lon = 6.5
        val forecast = Forecast(City(0,"name",Coord(lon,lat),"US",0,0))
        weatherService.forecastResponse = forecast
        viewModel.fetchForecast(lat,lon)

        assertEquals(forecast,viewModel.forecast.value)

        weatherService.shouldFail = true
        viewModel.fetchForecast(lat,lon)

        assertNotNull(viewModel.errorMessage.value)
    }

}