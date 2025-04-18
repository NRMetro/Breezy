package com.example.breezy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class LocationViewModel() :ViewModel(){
    private var _location = MutableLiveData<Location?>(null)
    val location: LiveData<Location?> = _location

    fun updateLocation(newLocation: Location) {
        if(newLocation.latitude != _location.value?.latitude && newLocation.longitude != _location.value?.longitude){
            _location.value = newLocation
        }

    }

    fun fetchLocation( service: LocationService?) {

        Log.d("LocationService", "Service is NOT null, calling fetchLocationAndWeather()")
        service?.fetchLocationAndWeather { location ->
            Log.d("LocationService", "Got location: $location")
        }

    }


}

