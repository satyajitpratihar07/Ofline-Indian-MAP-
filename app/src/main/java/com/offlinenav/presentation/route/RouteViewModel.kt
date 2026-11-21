package com.offlinenav.presentation.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.*
import com.offlinenav.domain.repository.LocationRepository
import com.offlinenav.domain.repository.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RouteUiState(
    val origin: Location = Location(28.6139, 77.2090, "Current Location"),
    val destination: Location = Location(28.7041, 77.1025, "Destination"),
    val routes: List<Route> = emptyList(),
    val selectedRouteIndex: Int = 0,
    val selectedTravelMode: TravelMode = TravelMode.CAR,
    val selectedRouteType: RouteType = RouteType.FASTEST,
    val avoidTolls: Boolean = false,
    val avoidHighways: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedRoute: Route?
        get() = routes.getOrNull(selectedRouteIndex)
}

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState.asStateFlow()

    fun loadRoutes(destination: Location) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, destination = destination)
            val userLoc = locationRepository.getLastKnownLocation() ?: _uiState.value.origin
            _uiState.value = _uiState.value.copy(origin = userLoc)

            calculateCurrentRoutes()
        }
    }

    fun setTravelMode(mode: TravelMode) {
        _uiState.value = _uiState.value.copy(selectedTravelMode = mode)
        calculateCurrentRoutes()
    }

    fun selectRoute(index: Int) {
        _uiState.value = _uiState.value.copy(selectedRouteIndex = index)
    }

    fun toggleAvoidTolls() {
        _uiState.value = _uiState.value.copy(avoidTolls = !_uiState.value.avoidTolls)
        calculateCurrentRoutes()
    }

    fun toggleAvoidHighways() {
        _uiState.value = _uiState.value.copy(avoidHighways = !_uiState.value.avoidHighways)
        calculateCurrentRoutes()
    }

    private fun calculateCurrentRoutes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val state = _uiState.value

            val result = routeRepository.calculateRoutes(
                origin = state.origin,
                destination = state.destination,
                travelMode = state.selectedTravelMode,
                routeType = state.selectedRouteType,
                avoidTolls = state.avoidTolls,
                avoidHighways = state.avoidHighways
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    routes = result.getOrThrow(),
                    selectedRouteIndex = 0,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Could not calculate route"
                )
            }
        }
    }
}
