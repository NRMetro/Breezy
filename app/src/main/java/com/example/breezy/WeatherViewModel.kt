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
    private val apiKey :String,
    private val zipService: ZipService

):ViewModel() {
    private var _weather: MutableLiveData<CurrentWeather> = MutableLiveData()
    val weather: LiveData<CurrentWeather> = _weather

    private var _coords: MutableLiveData<ZipCoords> = MutableLiveData()
    val coords: LiveData<ZipCoords> = _coords

    fun fetchWeather(latitude: Double, longitude:Double){
        val call = weatherService.getWeather(latitude = latitude, longitude = longitude,apiKey = apiKey, unitType = "imperial")
        call.enqueue(object: Callback<CurrentWeather>{
            override fun onResponse(p0: Call<CurrentWeather>, p1: Response<CurrentWeather>) {
                _weather.value = p1.body()
            }

            override fun onFailure(p0: Call<CurrentWeather>, p1: Throwable) {
                Log.e("Weather", "Failed Call", p1)
            }
        })
    }

    fun fetchCoords(zipCode:Int){
        val call = zipService.getZip(zipCode = zipCode,apiKey = apiKey)

        call.enqueue(object: Callback<ZipCoords>{
            override fun onResponse(p0: Call<ZipCoords>, p1: Response<ZipCoords>) {
                _coords.value = p1.body()
            }

            override fun onFailure(p0: Call<ZipCoords>, p1: Throwable) {
                Log.e("Weather", "Failed Call", p1)
            }
        })
    }
}