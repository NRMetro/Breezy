package com.example.breezy.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val dispatcher: CoroutineDispatcher
) :ViewModel(){
    private var _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    fun updateLocation(newLocation: Location) = viewModelScope.launch(dispatcher) {
        if(newLocation.latitude != _location.value?.latitude && newLocation.longitude != _location.value?.longitude){
            _location.value = newLocation
        }

    }

}

