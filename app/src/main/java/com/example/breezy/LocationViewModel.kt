package com.example.breezy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat


class LocationViewModel() {

    fun checkOrRequestLocationPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<String, Boolean>,
        launchLocations: ()-> Unit
    ){
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if(permissionCheckResult == PackageManager.PERMISSION_GRANTED){
            launchLocations()
        }
        else{
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}

