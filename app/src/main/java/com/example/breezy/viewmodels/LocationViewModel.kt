package com.example.breezy.viewmodels

import android.location.Location
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

}

