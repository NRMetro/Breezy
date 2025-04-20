package com.example.breezy.serialobjects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val forecastItem = WeatherList(0L,0L,0L,Temp(0.0,0.0,0.0,0.0,0.0,0.0),
    FeelsLike(0.0,0.0,0.0,0.0),0,0,arrayListOf(Weather(0,"","","")),0.0,0,0.0,0,
    0.0,0.0,0.0)

@Serializable
data class Forecast(
    val city: City = City(0,"",Coord(0.0,0.0),"",0,0),
    val cod: Int = 0,
    val message: Double = 0.0,
    val cnt: Int = 0,
    val list: ArrayList<WeatherList> = arrayListOf(forecastItem,forecastItem,forecastItem,forecastItem,forecastItem,forecastItem,forecastItem,
        forecastItem,forecastItem,forecastItem,forecastItem,forecastItem,forecastItem,forecastItem)
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
