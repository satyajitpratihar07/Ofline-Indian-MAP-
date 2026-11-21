package com.offlinenav.presentation.speedometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.SpeedUnit
import com.offlinenav.domain.model.TripStats
import com.offlinenav.domain.repository.LocationRepository
import com.offlinenav.domain.repository.SettingsRepository
import com.offlinenav.gps.speed.TripStatisticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpeedometerUiState(
    val tripStats: TripStats = TripStats(),
    val speedUnit: SpeedUnit = SpeedUnit.KM_H,
    val speedLimitWarningKmh: Int = 100,
    val isOverSpeedLimit: Boolean = false
)

@HiltViewModel
class SpeedometerViewModel @Inject constructor(
    private val tripStatisticsTracker: TripStatisticsTracker,
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeedometerUiState())
    val uiState: StateFlow<SpeedometerUiState> = _uiState.asStateFlow()

    init {
        tripStatisticsTracker.tripStats
            .onEach { stats ->
                val limit = _uiState.value.speedLimitWarningKmh
                _uiState.value = _uiState.value.copy(
                    tripStats = stats,
                    isOverSpeedLimit = stats.currentSpeedKmh > limit
                )
            }
            .launchIn(viewModelScope)

        settingsRepository.speedUnit
            .onEach { unit -> _uiState.value = _uiState.value.copy(speedUnit = unit) }
            .launchIn(viewModelScope)
    }

    fun resetTrip() {
        tripStatisticsTracker.reset()
    }

    fun toggleSpeedUnit() {
        val newUnit = if (_uiState.value.speedUnit == SpeedUnit.KM_H) SpeedUnit.MPH else SpeedUnit.KM_H
        viewModelScope.launch {
            settingsRepository.setSpeedUnit(newUnit)
        }
    }
}
