package com.example.breezy.services

import com.example.breezy.serialobjects.ZipCoords
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ZipService {
    @GET("zip")
    suspend fun getZip(
        @Query("zip") zipCode: Int,
        @Query("appid") apiKey: String
    ): Response<ZipCoords>

    companion object{
        val zipService: ZipService

        init{
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            this.zipService = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/geo/1.0/")
                .client(client)
                .addConverterFactory(
                    Json.asConverterFactory(
                        "application/json".toMediaType()
                    ))
                .build()
                .create(ZipService::class.java)
        }
    }
}