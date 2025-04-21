package com.example.breezy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breezy.R
import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.Forecast
import com.example.breezy.serialobjects.ZipCoords
import com.example.breezy.services.WeatherService
import com.example.breezy.services.ZipService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class WeatherViewModel (
    private val weatherService: WeatherService,
    private val apiKey :String,
    private val zipService: ZipService,
    private val dispatcher: CoroutineDispatcher,
):ViewModel() {

    private var _weather: MutableStateFlow<CurrentWeather> = MutableStateFlow(CurrentWeather())
    val weather: StateFlow<CurrentWeather> = _weather

    private var _coords: MutableStateFlow<ZipCoords> = MutableStateFlow(ZipCoords())
    val coords: StateFlow<ZipCoords> = _coords

    private var _forecast: MutableStateFlow<Forecast> = MutableStateFlow(Forecast())
    val forecast: StateFlow<Forecast> = _forecast

    private var _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchWeather(latitude: Double, longitude:Double) = viewModelScope.launch(dispatcher){
        if(latitude != 0.0 ){

            val response = weatherService.getWeather(latitude = latitude, longitude = longitude,apiKey = apiKey, unitType = "imperial")
            if (response.isSuccessful) {
                _weather.value = response.body()!!

            }
            else{
                _errorMessage.value  = "Failed to get weather at latitude $latitude and longitude $longitude"
            }
        }
    }

    fun fetchCoords(zipCode:Int) = viewModelScope.launch(dispatcher){
        val response = zipService.getZip(zipCode = zipCode,apiKey = apiKey)
        if (response.isSuccessful) {
            _coords.value = response.body()!!
        }
        else{
            _errorMessage.value = "Failed to find zip code $zipCode"
        }

    }

    fun errorShown(){
        _errorMessage.value = null
    }

    fun fetchForecast(latitude: Double, longitude:Double) = viewModelScope.launch(dispatcher){
        val response = weatherService.getForecast(latitude = latitude, longitude = longitude,apiKey = apiKey, unitType = "imperial", count = 14)

        if (response.isSuccessful) {
            _forecast.value = response.body()!!
        }
        else{
            _errorMessage.value  = "Failed to get forecast at latitude $latitude and longitude $longitude"
        }

    }

    fun weatherIcon(): Int {
        var weatherType = ""
        weatherType = this.weather.value.weather[0].main

        return weatherIcon(weatherType)
    }

    fun weatherIcon(weatherType:String): Int {
        var image = R.drawable.sun
        if (weatherType == "Snow"){
            image = R.drawable.snow
        }
        else if(weatherType == "Rain"){
            image = R.drawable.rain
        }
        else if(weatherType == "Clouds"){
            image = R.drawable.partly_cloudy
        }

        return image
    }

}