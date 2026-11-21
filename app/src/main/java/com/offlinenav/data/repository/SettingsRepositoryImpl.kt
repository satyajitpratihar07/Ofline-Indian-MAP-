package com.offlinenav.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.offlinenav.domain.model.DistanceUnit
import com.offlinenav.domain.model.SpeedUnit
import com.offlinenav.domain.model.TravelMode
import com.offlinenav.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("offlinenav_prefs", Context.MODE_PRIVATE)

    private val _speedUnit = MutableStateFlow(
        SpeedUnit.valueOf(prefs.getString("speed_unit", SpeedUnit.KM_H.name) ?: SpeedUnit.KM_H.name)
    )
    override val speedUnit: Flow<SpeedUnit> = _speedUnit.asStateFlow()

    private val _distanceUnit = MutableStateFlow(
        DistanceUnit.valueOf(prefs.getString("distance_unit", DistanceUnit.KILOMETERS.name) ?: DistanceUnit.KILOMETERS.name)
    )
    override val distanceUnit: Flow<DistanceUnit> = _distanceUnit.asStateFlow()

    private val _defaultTravelMode = MutableStateFlow(
        TravelMode.valueOf(prefs.getString("travel_mode", TravelMode.CAR.name) ?: TravelMode.CAR.name)
    )
    override val defaultTravelMode: Flow<TravelMode> = _defaultTravelMode.asStateFlow()

    private val _avoidTolls = MutableStateFlow(prefs.getBoolean("avoid_tolls", false))
    override val avoidTolls: Flow<Boolean> = _avoidTolls.asStateFlow()

    private val _avoidHighways = MutableStateFlow(prefs.getBoolean("avoid_highways", false))
    override val avoidHighways: Flow<Boolean> = _avoidHighways.asStateFlow()

    private val _isDarkMode = MutableStateFlow(
        if (prefs.contains("dark_mode")) prefs.getBoolean("dark_mode", true) else null
    )
    override val isDarkMode: Flow<Boolean?> = _isDarkMode.asStateFlow()

    private val _voiceEnabled = MutableStateFlow(prefs.getBoolean("voice_enabled", true))
    override val voiceEnabled: Flow<Boolean> = _voiceEnabled.asStateFlow()

    private val _voiceLanguage = MutableStateFlow(prefs.getString("voice_lang", "English") ?: "English")
    override val voiceLanguage: Flow<String> = _voiceLanguage.asStateFlow()

    private val _voiceVolume = MutableStateFlow(prefs.getFloat("voice_vol", 1.0f))
    override val voiceVolume: Flow<Float> = _voiceVolume.asStateFlow()

    private val _speedWarningEnabled = MutableStateFlow(prefs.getBoolean("speed_warn", true))
    override val speedWarningEnabled: Flow<Boolean> = _speedWarningEnabled.asStateFlow()

    private val _speedLimitWarningKmh = MutableStateFlow(prefs.getInt("speed_limit_kmh", 100))
    override val speedLimitWarningKmh: Flow<Int> = _speedLimitWarningKmh.asStateFlow()

    private val _is3DBuildingsEnabled = MutableStateFlow(prefs.getBoolean("3d_buildings", true))
    override val is3DBuildingsEnabled: Flow<Boolean> = _is3DBuildingsEnabled.asStateFlow()

    override suspend fun setSpeedUnit(unit: SpeedUnit) {
        prefs.edit().putString("speed_unit", unit.name).apply()
        _speedUnit.value = unit
    }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        prefs.edit().putString("distance_unit", unit.name).apply()
        _distanceUnit.value = unit
    }

    override suspend fun setDefaultTravelMode(mode: TravelMode) {
        prefs.edit().putString("travel_mode", mode.name).apply()
        _defaultTravelMode.value = mode
    }

    override suspend fun setAvoidTolls(avoid: Boolean) {
        prefs.edit().putBoolean("avoid_tolls", avoid).apply()
        _avoidTolls.value = avoid
    }

    override suspend fun setAvoidHighways(avoid: Boolean) {
        prefs.edit().putBoolean("avoid_highways", avoid).apply()
        _avoidHighways.value = avoid
    }

    override suspend fun setDarkMode(isDark: Boolean?) {
        if (isDark == null) prefs.edit().remove("dark_mode").apply()
        else prefs.edit().putBoolean("dark_mode", isDark).apply()
        _isDarkMode.value = isDark
    }

    override suspend fun setVoiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("voice_enabled", enabled).apply()
        _voiceEnabled.value = enabled
    }

    override suspend fun setVoiceLanguage(language: String) {
        prefs.edit().putString("voice_lang", language).apply()
        _voiceLanguage.value = language
    }

    override suspend fun setVoiceVolume(volume: Float) {
        prefs.edit().putFloat("voice_vol", volume).apply()
        _voiceVolume.value = volume
    }

    override suspend fun setSpeedWarningEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("speed_warn", enabled).apply()
        _speedWarningEnabled.value = enabled
    }

    override suspend fun setSpeedLimitWarningKmh(kmh: Int) {
        prefs.edit().putInt("speed_limit_kmh", kmh).apply()
        _speedLimitWarningKmh.value = kmh
    }

    override suspend fun set3DBuildingsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("3d_buildings", enabled).apply()
        _is3DBuildingsEnabled.value = enabled
    }
}
