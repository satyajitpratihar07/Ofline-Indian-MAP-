package com.offlinenav.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.DistanceUnit
import com.offlinenav.domain.model.SpeedUnit
import com.offlinenav.domain.model.TravelMode
import com.offlinenav.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val speedUnit: SpeedUnit = SpeedUnit.KM_H,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val travelMode: TravelMode = TravelMode.CAR,
    val avoidTolls: Boolean = false,
    val avoidHighways: Boolean = false,
    val isDarkMode: Boolean? = true,
    val voiceEnabled: Boolean = true,
    val voiceLanguage: String = "English",
    val speedLimitWarningKmh: Int = 100,
    val is3DBuildingsEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        settingsRepository.speedUnit.onEach { _uiState.value = _uiState.value.copy(speedUnit = it) }.launchIn(viewModelScope)
        settingsRepository.distanceUnit.onEach { _uiState.value = _uiState.value.copy(distanceUnit = it) }.launchIn(viewModelScope)
        settingsRepository.defaultTravelMode.onEach { _uiState.value = _uiState.value.copy(travelMode = it) }.launchIn(viewModelScope)
        settingsRepository.avoidTolls.onEach { _uiState.value = _uiState.value.copy(avoidTolls = it) }.launchIn(viewModelScope)
        settingsRepository.avoidHighways.onEach { _uiState.value = _uiState.value.copy(avoidHighways = it) }.launchIn(viewModelScope)
        settingsRepository.isDarkMode.onEach { _uiState.value = _uiState.value.copy(isDarkMode = it) }.launchIn(viewModelScope)
        settingsRepository.voiceEnabled.onEach { _uiState.value = _uiState.value.copy(voiceEnabled = it) }.launchIn(viewModelScope)
        settingsRepository.speedLimitWarningKmh.onEach { _uiState.value = _uiState.value.copy(speedLimitWarningKmh = it) }.launchIn(viewModelScope)
        settingsRepository.is3DBuildingsEnabled.onEach { _uiState.value = _uiState.value.copy(is3DBuildingsEnabled = it) }.launchIn(viewModelScope)
    }

    fun setDarkMode(isDark: Boolean?) = viewModelScope.launch { settingsRepository.setDarkMode(isDark) }
    fun setSpeedUnit(unit: SpeedUnit) = viewModelScope.launch { settingsRepository.setSpeedUnit(unit) }
    fun setDistanceUnit(unit: DistanceUnit) = viewModelScope.launch { settingsRepository.setDistanceUnit(unit) }
    fun setAvoidTolls(avoid: Boolean) = viewModelScope.launch { settingsRepository.setAvoidTolls(avoid) }
    fun setAvoidHighways(avoid: Boolean) = viewModelScope.launch { settingsRepository.setAvoidHighways(avoid) }
    fun setVoiceEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setVoiceEnabled(enabled) }
    fun setSpeedLimitWarningKmh(kmh: Int) = viewModelScope.launch { settingsRepository.setSpeedLimitWarningKmh(kmh) }
    fun set3DBuildings(enabled: Boolean) = viewModelScope.launch { settingsRepository.set3DBuildingsEnabled(enabled) }
}
