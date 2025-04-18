package com.example.breezy.serialobjects

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