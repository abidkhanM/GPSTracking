package com.artificient.gpstracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*

class LocationUpdatesManager(
    private val context: Context,
    private val intervalMillis: Long = 5000L, // default 5s
    private val onLocation: (lat: Double, lng: Double, speed: Float) -> Unit
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
        .setMinUpdateIntervalMillis(intervalMillis)
        .build()
    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun start() {
        if (callback != null) return
        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    onLocation(loc.latitude, loc.longitude, loc.speed)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    fun stop() {
        callback?.let { fusedLocationClient.removeLocationUpdates(it) }
        callback = null
    }
} 