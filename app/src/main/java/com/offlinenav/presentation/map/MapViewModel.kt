package com.offlinenav.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.Route
import com.offlinenav.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

data class MapUiState(
    val userLocation: Location = Location(28.6139, 77.2090, "New Delhi"),
    val activeRoute: Route? = null,
    val is3DMode: Boolean = false,
    val isTrackingUser: Boolean = true,
    val compassBearing: Float = 0f,
    val mapPitch: Double = 0.0,
    val zoomLevel: Double = 15.0
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        startLocationUpdates()
    }

    fun startLocationUpdates() {
        locationRepository.getLocationUpdates(1000L)
            .catch { e -> Timber.w(e, "Location updates not available: ${e.message}") }
            .onEach { loc ->
                _uiState.value = _uiState.value.copy(
                    userLocation = loc,
                    compassBearing = loc.bearing
                )
            }
            .launchIn(viewModelScope)
    }

    fun toggle3DMode() {
        val current3D = _uiState.value.is3DMode
        _uiState.value = _uiState.value.copy(
            is3DMode = !current3D,
            mapPitch = if (!current3D) 55.0 else 0.0
        )
    }

    fun recenterOnUser() {
        _uiState.value = _uiState.value.copy(isTrackingUser = true)
        startLocationUpdates()
    }

    fun setRoute(route: Route?) {
        _uiState.value = _uiState.value.copy(activeRoute = route)
    }
}
