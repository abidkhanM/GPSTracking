package com.artificient.gpstracking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artificient.gpstracking.data.TrackingRepository
import com.artificient.gpstracking.data.db.LocationPointEntity
import com.artificient.gpstracking.data.db.TripEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class TrackingState {
    object Idle : TrackingState()
    object Tracking : TrackingState()
    object Paused : TrackingState()
    object Finished : TrackingState()
}

data class TrackingMetrics(
    val speed: Float = 0f,
    val distance: Float = 0f,
    val elapsedMillis: Long = 0L
)

class TrackingViewModel(
    private val repository: TrackingRepository
) : ViewModel() {
    private val _trackingState = MutableStateFlow<TrackingState>(TrackingState.Idle)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    private val _metrics = MutableStateFlow(TrackingMetrics())
    val metrics: StateFlow<TrackingMetrics> = _metrics.asStateFlow()

    private var currentTripId: Long? = null
    private var startTime: Long = 0L
    private var lastLocation: LocationPointEntity? = null

    fun startTracking() {
        viewModelScope.launch {
            startTime = System.currentTimeMillis()
            val tripId = repository.insertTrip(
                TripEntity(
                    startTime = startTime,
                    endTime = null,
                    distanceMeters = 0f,
                    durationMillis = 0L
                )
            )
            currentTripId = tripId
            _trackingState.value = TrackingState.Tracking
            _metrics.value = TrackingMetrics()
            // TODO: Start location updates
        }
    }

    fun pauseTracking() {
        _trackingState.value = TrackingState.Paused
        // TODO: Pause location updates
    }

    fun resumeTracking() {
        _trackingState.value = TrackingState.Tracking
        // TODO: Resume location updates
    }

    fun stopTracking() {
        viewModelScope.launch {
            val tripId = currentTripId ?: return@launch
            val now = System.currentTimeMillis()
            val metrics = _metrics.value
            repository.updateTrip(
                TripEntity(
                    id = tripId,
                    startTime = startTime,
                    endTime = now,
                    distanceMeters = metrics.distance,
                    durationMillis = metrics.elapsedMillis
                )
            )
            _trackingState.value = TrackingState.Finished
            // TODO: Stop location updates
        }
    }

    // This would be called by location updates
    fun onLocationUpdate(lat: Double, lng: Double, speed: Float) {
        viewModelScope.launch {
            val tripId = currentTripId ?: return@launch
            val now = System.currentTimeMillis()
            val point = LocationPointEntity(
                tripId = tripId,
                timestamp = now,
                latitude = lat,
                longitude = lng,
                speed = speed
            )
            repository.insertLocationPoint(point)
            // Update metrics (distance, speed, elapsed)
            val last = lastLocation
            val distance = if (last != null) {
                _metrics.value.distance + haversine(last.latitude, last.longitude, lat, lng)
            } else {
                _metrics.value.distance
            }
            val elapsed = now - startTime
            _metrics.value = TrackingMetrics(
                speed = speed,
                distance = distance,
                elapsedMillis = elapsed
            )
            lastLocation = point
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val R = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (R * c).toFloat()
    }
} 