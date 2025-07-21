package com.artificient.gpstracking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artificient.gpstracking.data.TrackingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: TrackingRepository
) : ViewModel() {
    private val _updateInterval = MutableStateFlow(5)
    val updateInterval: StateFlow<Int> = _updateInterval.asStateFlow()

    private val _backgroundTracking = MutableStateFlow(false)
    val backgroundTracking: StateFlow<Boolean> = _backgroundTracking.asStateFlow()

    init {
        viewModelScope.launch {
            repository.updateIntervalFlow.collectLatest { _updateInterval.value = it }
        }
        viewModelScope.launch {
            repository.backgroundTrackingFlow.collectLatest { _backgroundTracking.value = it }
        }
    }

    fun setUpdateInterval(seconds: Int) {
        viewModelScope.launch { repository.setUpdateInterval(seconds) }
    }

    fun setBackgroundTracking(enabled: Boolean) {
        viewModelScope.launch { repository.setBackgroundTracking(enabled) }
    }
} 