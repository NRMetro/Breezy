package com.example.breezy.services

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
import com.example.breezy.R
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
    private var weatherName: String = "Loading"
    private var weatherTemp: String = "Loading"
    private var weatherCondition: String = "Loading"
    private var weatherIcon: Int = R.drawable.sun
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

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

        val notifcationText = getString(R.string.notification,weatherName,weatherTemp,weatherCondition)
        return NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle("Current Weather")
            .setContentText(notifcationText)
            .setSmallIcon(weatherIcon)
            .build()

    }

    suspend fun fetchWeather(lat: Double, lon: Double) {
        val apiKey = getString(R.string.weather_api_key)
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=imperial&appid=$apiKey"

        val response = withContext(Dispatchers.IO) {
            URL(url).readText()
        }

        val json = JSONObject(response)
        weatherName = json.getString("name").toString()
        weatherTemp = json.getJSONObject("main").getDouble("temp").toString()
        weatherCondition = json.getJSONArray("weather").getJSONObject(0).getString("main")
        weatherIcon = weatherIcon(weatherCondition)
    }

    fun weatherIcon(weatherType:String): Int {
        var image = R.drawable.sun
        if (weatherType == "Snow"){
            image = R.drawable.snow
        }
        else if(weatherType == "Rain"){
            image = R.drawable.rain
        }
        else if(weatherType == "Clouds"){
            image = R.drawable.partly_cloudy
        }

        return image
    }
}