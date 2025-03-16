package com.example.breezy


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel (
    private val weatherService: WeatherService,
    private val apiKey :String
):ViewModel() {
    private var _weather: MutableLiveData<CurrentWeather> = MutableLiveData()
    val weather: LiveData<CurrentWeather> = _weather

    fun fetchWeather(){
        val call = weatherService.getWeather(latitude = 44.90, longitude = -93.56,apiKey = apiKey, unitType = "imperial")
        call.enqueue(object: Callback<CurrentWeather>{
            override fun onResponse(p0: Call<CurrentWeather>, p1: Response<CurrentWeather>) {
                _weather.value = p1.body()
            }

            override fun onFailure(p0: Call<CurrentWeather>, p1: Throwable) {
                Log.e("Weather", "Failed Call", p1)
            }
        })
    }
}