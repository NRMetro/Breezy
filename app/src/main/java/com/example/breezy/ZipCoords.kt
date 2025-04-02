package com.example.breezy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZipCoords(
    val zip: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
){

}