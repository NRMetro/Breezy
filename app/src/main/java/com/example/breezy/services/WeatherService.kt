package com.example.breezy.services

import com.example.breezy.serialobjects.CurrentWeather
import com.example.breezy.serialobjects.Forecast
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") unitType: String
    ): Response<CurrentWeather>

    @GET("forecast/daily")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") unitType: String,
        @Query("cnt") count: Int
    ): Response<Forecast>

    companion object{
        val weatherService: WeatherService

        init{
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            this.weatherService = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .client(client)
                .addConverterFactory(
                    Json.asConverterFactory(
                        "application/json".toMediaType()
                    ))
                .build()
                .create(WeatherService::class.java)
        }
    }
}