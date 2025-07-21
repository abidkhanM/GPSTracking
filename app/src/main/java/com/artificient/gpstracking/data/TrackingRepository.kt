package com.artificient.gpstracking.data

import com.artificient.gpstracking.data.db.*
import com.artificient.gpstracking.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackingRepository(
    private val tripDao: TripDao,
    private val settingsDataStore: SettingsDataStore
) {
    // Room: Trips
    fun getAllTrips(): Flow<List<TripEntity>> = tripDao.getAllTrips()
    fun getTripWithPoints(tripId: Long): Flow<TripWithPoints?> =
        tripDao.getTripsWithPoints().map { list -> list.find { it.trip.id == tripId } }
    fun getAllTripsWithPoints(): Flow<List<TripWithPoints>> = tripDao.getTripsWithPoints()
    suspend fun insertTrip(trip: TripEntity): Long = tripDao.insertTrip(trip)
    suspend fun insertLocationPoint(point: LocationPointEntity) = tripDao.insertLocationPoint(point)
    suspend fun updateTrip(trip: TripEntity) = tripDao.updateTrip(trip)
    fun getPointsForTrip(tripId: Long): Flow<List<LocationPointEntity>> = tripDao.getPointsForTrip(tripId)

    // DataStore: Settings
    val updateIntervalFlow: Flow<Int> = settingsDataStore.updateIntervalFlow
    val backgroundTrackingFlow: Flow<Boolean> = settingsDataStore.backgroundTrackingFlow
    suspend fun setUpdateInterval(seconds: Int) = settingsDataStore.setUpdateInterval(seconds)
    suspend fun setBackgroundTracking(enabled: Boolean) = settingsDataStore.setBackgroundTracking(enabled)
} 