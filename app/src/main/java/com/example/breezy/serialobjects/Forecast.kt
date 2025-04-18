package com.example.breezy.serialobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val city: City,
    val cod: Int,
    val message: Double,
    val cnt: Int,
    val list: ArrayList<WeatherList>
)

@Serializable
data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int
)

@Serializable
data class WeatherList(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    @SerialName("feels_like")
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val weather: ArrayList<Weather>,
    val speed: Double,
    val deg: Int,
    val gust: Double,
    val clouds: Int,
    val pop: Double,
    val rain: Double? = 0.0,
    val snow: Double? = 0.0
)

@Serializable
data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

@Serializable
data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)
