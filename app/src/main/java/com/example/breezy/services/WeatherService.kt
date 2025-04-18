package com.example.breezy.services

import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.Forecast
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") unitType: String
    ): Call<CurrentWeather>

    @GET("forecast/daily")
    fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") unitType: String,
        @Query("cnt") count: Int
    ): Call<Forecast>

}