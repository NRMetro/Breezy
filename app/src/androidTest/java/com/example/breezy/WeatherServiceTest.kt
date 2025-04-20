package com.example.breezy

import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.Forecast
import com.example.breezy.services.WeatherService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Response

class WeatherServiceTest :WeatherService {
    val actions = mutableListOf<String>()
    var weatherResponse: CurrentWeather? = null
    var forecastResponse: Forecast? = null
    var shouldFail = false

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        unitType: String
    ): Response<CurrentWeather> {
        if(shouldFail){
            actions.add("Failed Weather")
            return Response.error(404,ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"Not found\"}"))
        }
        else{
            actions.add("Get Weather")
            return Response.success(weatherResponse)
        }

    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        unitType: String,
        count: Int
    ): Response<Forecast> {
        if(shouldFail){
            actions.add("Failed Forecast")
            return Response.error(404,ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"Not found\"}"))
        }
        else{
            actions.add("Get Forecast")
            return Response.success(forecastResponse)
        }

    }
}