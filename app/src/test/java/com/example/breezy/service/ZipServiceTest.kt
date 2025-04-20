package com.example.breezy.service

import com.example.breezy.serialobjects.Coord
import com.example.breezy.serialobjects.ZipCoords
import com.example.breezy.services.ZipService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ZipServiceTest :ZipService {
    val actions = mutableListOf<String>()
    var zipServiceResponse: ZipCoords? = null

    var shouldFailZip = false

    override suspend fun getZip(zipCode: Int, apiKey: String): Response<ZipCoords> {
        if(shouldFailZip){
            actions.add("Fail Weather")
            return Response.error(404, ResponseBody.create("application/json".toMediaTypeOrNull(), "{\"error\":\"Not found\"}"))
        }
        else{
            actions.add("Get Weather")
            return Response.success(zipServiceResponse)
        }
    }

}