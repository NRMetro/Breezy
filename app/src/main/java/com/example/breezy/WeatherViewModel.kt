package com.example.breezy


import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    private var _forecast: MutableLiveData<Forecast> = MutableLiveData()
    val forecast: LiveData<Forecast> = _forecast

    private var _errorMessage: MutableState<String?> = mutableStateOf(null)
    val errorMessage: State<String?> = _errorMessage

    fun fetchWeather(latitude: Double, longitude:Double){
        val call = weatherService.getWeather(latitude = latitude, longitude = longitude,apiKey = apiKey, unitType = "imperial")
        call.enqueue(object: Callback<CurrentWeather>{
            override fun onResponse(p0: Call<CurrentWeather>, p1: Response<CurrentWeather>) {
                _weather.value = p1.body()
            }

            override fun onFailure(p0: Call<CurrentWeather>, p1: Throwable) {
                _errorMessage.value  = "Failed to get weather at latitude $latitude and longitude $longitude"
                Log.e("Weather", "Failed Call", p1)
            }
        })
    }

    fun fetchCoords(zipCode:Int){
        val call = zipService.getZip(zipCode = zipCode,apiKey = apiKey)

        call.enqueue(object: Callback<ZipCoords>{
            override fun onResponse(p0: Call<ZipCoords>, p1: Response<ZipCoords>) {
                if(p1.message() == "OK"){
                    _coords.value = p1.body()
                }
                else{
                    _errorMessage.value = "Zip " + p1.message()
                }

            }

            override fun onFailure(p0: Call<ZipCoords>, p1: Throwable) {
                _errorMessage.value = "Failed to find zip code $zipCode"
                Log.e("Weather", "Failed Call", p1)
            }
        })
    }

    fun errorShown(){
        _errorMessage.value = null
    }

    fun fetchForecast(latitude: Double, longitude:Double){
        val call = weatherService.getForecast(latitude = latitude, longitude = longitude,apiKey = apiKey, unitType = "imperial", count = 14)
        call.enqueue(object: Callback<Forecast>{
            override fun onResponse(p0: Call<Forecast>, p1: Response<Forecast>) {
                _forecast.value = p1.body()
            }

            override fun onFailure(p0: Call<Forecast>, p1: Throwable) {
                _errorMessage.value  = "Failed to get forecast at latitude $latitude and longitude $longitude"
                Log.e("Weather", "Failed Call", p1)
            }
        })
    }
}