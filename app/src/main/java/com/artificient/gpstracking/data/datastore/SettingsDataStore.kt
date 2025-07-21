package com.artificient.gpstracking.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {
    companion object {
        private const val DATASTORE_NAME = "settings"
        private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
        val KEY_UPDATE_INTERVAL = intPreferencesKey("location_update_interval")
        val KEY_BACKGROUND_TRACKING = booleanPreferencesKey("background_tracking")
        const val DEFAULT_UPDATE_INTERVAL = 5 // seconds
        const val DEFAULT_BACKGROUND_TRACKING = false
    }

    val updateIntervalFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_UPDATE_INTERVAL] ?: DEFAULT_UPDATE_INTERVAL
    }

    val backgroundTrackingFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_BACKGROUND_TRACKING] ?: DEFAULT_BACKGROUND_TRACKING
    }

    suspend fun setUpdateInterval(seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_UPDATE_INTERVAL] = seconds
        }
    }

    suspend fun setBackgroundTracking(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_BACKGROUND_TRACKING] = enabled
        }
    }
} 