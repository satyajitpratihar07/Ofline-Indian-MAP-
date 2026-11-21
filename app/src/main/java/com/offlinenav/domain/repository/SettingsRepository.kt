package com.offlinenav.domain.repository

import com.offlinenav.domain.model.DistanceUnit
import com.offlinenav.domain.model.SpeedUnit
import com.offlinenav.domain.model.TravelMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val speedUnit: Flow<SpeedUnit>
    val distanceUnit: Flow<DistanceUnit>
    val defaultTravelMode: Flow<TravelMode>
    val avoidTolls: Flow<Boolean>
    val avoidHighways: Flow<Boolean>
    val isDarkMode: Flow<Boolean?> // null means system default
    val voiceEnabled: Flow<Boolean>
    val voiceLanguage: Flow<String>
    val voiceVolume: Flow<Float>
    val speedWarningEnabled: Flow<Boolean>
    val speedLimitWarningKmh: Flow<Int>
    val is3DBuildingsEnabled: Flow<Boolean>

    suspend fun setSpeedUnit(unit: SpeedUnit)
    suspend fun setDistanceUnit(unit: DistanceUnit)
    suspend fun setDefaultTravelMode(mode: TravelMode)
    suspend fun setAvoidTolls(avoid: Boolean)
    suspend fun setAvoidHighways(avoid: Boolean)
    suspend fun setDarkMode(isDark: Boolean?)
    suspend fun setVoiceEnabled(enabled: Boolean)
    suspend fun setVoiceLanguage(language: String)
    suspend fun setVoiceVolume(volume: Float)
    suspend fun setSpeedWarningEnabled(enabled: Boolean)
    suspend fun setSpeedLimitWarningKmh(kmh: Int)
    suspend fun set3DBuildingsEnabled(enabled: Boolean)
}
