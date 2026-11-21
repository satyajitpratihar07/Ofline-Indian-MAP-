package com.offlinenav.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.TripRecord
import com.offlinenav.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _trips = MutableStateFlow<List<TripRecord>>(emptyList())
    val trips: StateFlow<List<TripRecord>> = _trips.asStateFlow()

    init {
        tripRepository.getAllTrips()
            .onEach { list -> _trips.value = list }
            .launchIn(viewModelScope)
    }

    fun deleteTrip(id: Long) {
        viewModelScope.launch {
            tripRepository.deleteTrip(id)
        }
    }

    fun clearAllTrips() {
        viewModelScope.launch {
            tripRepository.clearAllTrips()
        }
    }
}
