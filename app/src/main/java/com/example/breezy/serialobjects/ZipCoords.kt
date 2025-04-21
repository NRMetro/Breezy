package com.example.breezy.serialobjects

import kotlinx.serialization.Serializable

@Serializable
data class ZipCoords(
    val zip: Int = 0,
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val country: String = ""
){

}