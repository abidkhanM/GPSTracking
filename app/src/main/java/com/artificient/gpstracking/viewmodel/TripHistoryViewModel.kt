package com.artificient.gpstracking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artificient.gpstracking.data.TrackingRepository
import com.artificient.gpstracking.data.db.TripWithPoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TripHistoryViewModel(
    private val repository: TrackingRepository
) : ViewModel() {
    private val _trips = MutableStateFlow<List<TripWithPoints>>(emptyList())
    val trips: StateFlow<List<TripWithPoints>> = _trips.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTripsWithPoints().collectLatest { list ->
                _trips.value = list
            }
        }
    }
} 