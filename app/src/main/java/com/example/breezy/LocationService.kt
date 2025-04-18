package com.example.breezy

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class LocationService: Service() {
    private val binder = LocalBinder()
    private lateinit var locationCallback: LocationCallback
    private var locationListener: ((Location) -> Unit)? = null
    private var location: Location? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var weatherTemp: String = "Loading"
    private var weatherCondition: String = "Loading"

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    // Location client
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "onStartCommand triggered")

        val initialNotification = NotificationCompat.Builder(this, "location_updates_channel")
            .setContentTitle("Starting Location Updates...")
            .setContentText("Waiting for location and weather data")
            .setSmallIcon(R.drawable.sun)
            .build()


        startForeground(1,initialNotification)
        startLocationUpdates()

        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationAndWeather(onResult: (Location?) -> Unit) {
        Log.d("LocationService", "Trying to fetch location...")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onResult(location)
            }
            .addOnFailureListener {
                Log.e("LocationService", "Failed to get location", it)
            }

        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){

//      Left at high interval for testing purposes
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 10000
            priority = PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val currentLocation = locationResult.lastLocation
                locationListener?.invoke(currentLocation)
                location = currentLocation
                serviceScope.launch {
                    fetchWeather(currentLocation.latitude, currentLocation.longitude)
                    val notification = updateNotification()
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, notification)
                }

                Log.d("LocationService","Found new location $currentLocation")
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
        Log.d("LocationService", "Requested location updates")

    }

    fun setOnLocationUpdateListener(listener: (Location) -> Unit) {
        locationListener = listener
    }

    private fun updateNotification() : Notification {
        val CHANNEL_DEFAULT_IMPORTANCE = "location_updates_channel"
        Log.d("LocationService", "Update Notification")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_DEFAULT_IMPORTANCE,
                "Location Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for location updates"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel) // Register the channel
        }


        return NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle("Location Updates")
            .setContentText("City ., Temp $weatherTemp\nWeather:$weatherCondition")
            .setSmallIcon(R.drawable.sun)
            .build()

    }

    suspend fun fetchWeather(lat: Double, lon: Double) {
        val apiKey = getString(R.string.weather_api_key)
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$apiKey"

        val response = withContext(Dispatchers.IO) {
            URL(url).readText() // Simplified for example
        }

        val json = JSONObject(response)
        val temp = json.getJSONObject("main").getDouble("temp").toString()
        val weather = json.getJSONArray("weather").getJSONObject(0).getString("main")

        weatherTemp = temp
        weatherCondition = weather

    }
}