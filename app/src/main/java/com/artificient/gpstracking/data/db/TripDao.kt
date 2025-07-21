package com.artificient.gpstracking.data.db

import androidx.room.*
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    @Insert
    suspend fun insertLocationPoint(point: LocationPointEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Transaction
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getPointsForTrip(tripId: Long): Flow<List<LocationPointEntity>>

    @Transaction
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getTripsWithPoints(): Flow<List<TripWithPoints>>
}

data class TripWithPoints(
    @Embedded val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tripId"
    )
    val points: List<LocationPointEntity>
) 