package com.artificient.gpstracking.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TripEntity::class, LocationPointEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
} 