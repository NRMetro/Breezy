package com.example.breezy

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ZipService {
    @GET("zip")
    fun getZip(
        @Query("zip") zipCode: Int,
        @Query("appid") apiKey: String
    ): Call<ZipCoords>
}