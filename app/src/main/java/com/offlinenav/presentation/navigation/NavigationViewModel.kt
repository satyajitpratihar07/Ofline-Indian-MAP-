package com.offlinenav.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.NavigationState
import com.offlinenav.domain.model.Route
import com.offlinenav.domain.model.TripRecord
import com.offlinenav.domain.repository.TripRepository
import com.offlinenav.navigation.engine.NavigationEngine
import com.offlinenav.navigation.voice.NavigationTtsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationEngine: NavigationEngine,
    private val ttsEngine: NavigationTtsEngine,
    private val tripRepository: TripRepository
) : ViewModel() {

    val navigationState: StateFlow<NavigationState> = navigationEngine.navigationState

    fun startNavigation(route: Route) {
        navigationEngine.startNavigation(route)
    }

    fun stopNavigation() {
        val state = navigationEngine.navigationState.value
        if (state is NavigationState.Active) {
            viewModelScope.launch {
                tripRepository.saveTrip(
                    TripRecord(
                        originName = state.route.origin.name.ifEmpty { "Start" },
                        destinationName = state.route.destination.name.ifEmpty { "Destination" },
                        startLatitude = state.route.origin.latitude,
                        startLongitude = state.route.origin.longitude,
                        endLatitude = state.route.destination.latitude,
                        endLongitude = state.route.destination.longitude,
                        startTime = System.currentTimeMillis() - 600_000L,
                        endTime = System.currentTimeMillis(),
                        totalDistanceMeters = state.route.distanceMeters,
                        durationSeconds = state.route.durationSeconds,
                        averageSpeedKmh = state.route.travelMode.speedKmhAverage,
                        maxSpeedKmh = state.currentSpeedKmh * 1.2f,
                        travelMode = state.route.travelMode
                    )
                )
            }
        }
        navigationEngine.stopNavigation()
    }

    fun toggleMute(): Boolean {
        val newMuted = !ttsEngine.isMuted()
        ttsEngine.setMuted(newMuted)
        return newMuted
    }

    fun isMuted(): Boolean = ttsEngine.isMuted()
}
