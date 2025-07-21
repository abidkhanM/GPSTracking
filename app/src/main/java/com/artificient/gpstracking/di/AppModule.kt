package com.artificient.gpstracking.di

import android.app.Application
import androidx.room.Room
import com.artificient.gpstracking.data.TrackingRepository
import com.artificient.gpstracking.data.db.AppDatabase
import com.artificient.gpstracking.data.datastore.SettingsDataStore
import com.artificient.gpstracking.viewmodel.SettingsViewModel
import com.artificient.gpstracking.viewmodel.TrackingViewModel
import com.artificient.gpstracking.viewmodel.TripHistoryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Room Database
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "gps_tracking_db"
        ).build()
    }
    single { get<AppDatabase>().tripDao() }

    // DataStore
    single { SettingsDataStore(androidApplication()) }

    // Repository
    single { TrackingRepository(get(), get()) }

    // ViewModels
    viewModel { TrackingViewModel(get()) }
    viewModel { TripHistoryViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
} 