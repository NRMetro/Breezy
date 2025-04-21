package com.example.breezy

import com.example.breezy.serialobjects.ZipCoords
import com.example.breezy.services.ZipService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class ZipServiceTest :ZipService {
    val actions = mutableListOf<String>()
    var zipServiceResponse: ZipCoords? = null
    var shouldFailZip = false

    override suspend fun getZip(zipCode: Int, apiKey: String): Response<ZipCoords> {
        if(shouldFailZip){
            actions.add("Fail Weather")
            return Response.error(404,
                "{\"error\":\"Not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        }
        else{
            actions.add("Get Weather")
            return Response.success(zipServiceResponse)
        }
    }

}