package com.example.breezy.serialobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val coord: Coord = Coord(-93.86,45.18),
    val weather: List<Weather> = listOf(Weather(0,"main","desc","")) ,
    val base: String = "",
    val main: Main = Main(0.0,0.0,0.0,0.0,0,0,0,0),
    val visibility: Int? = null,
    val wind: Wind = Wind(0.0,0),
    val rain: Rain? = null,
    val snow: Snow? = null,
    val clouds: Clouds = Clouds(0),
    val dt: Int = 0,
    val sys: Sys = Sys(0,0,"",0L,0L),
    val timezone: Int = 0,
    val id: Int = 0,
    val name: String = "",
    val cod: Int = 0

) {

}

@Serializable
data class Coord(
    val lon: Double,
    val lat: Double
)

@Serializable
data class Main(
    val temp: Double,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("temp_min")
    val tempMin: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerialName("sea_level")
    val seaLevel: Int,
    @SerialName("grnd_level")
    val groundLevel: Int
)

@Serializable
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class Rain(
    @SerialName("1h")
    val oneHour: Double
)

@Serializable
data class Snow(
    @SerialName("1h")
    val oneHour: Double
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class Sys(
    val type: Int? = -1,
    val id: Int? = -1,
    val country: String = "",
    val sunrise: Long,
    val sunset: Long
)