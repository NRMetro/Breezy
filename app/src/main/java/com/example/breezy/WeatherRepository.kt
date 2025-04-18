package com.example.breezy

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherRepository(private val weatherViewModel: WeatherViewModel) {
    private val _currentWeather = MutableStateFlow<CurrentWeather?>(null)
    val currentWeather: StateFlow<CurrentWeather?> = _currentWeather.asStateFlow()



    fun fetchWeather(location: Location) {
        weatherViewModel.fetchWeather(location.latitude,location.longitude)
        _currentWeather.value = weatherViewModel.weather.value
    }
}