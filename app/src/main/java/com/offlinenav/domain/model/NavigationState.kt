package com.offlinenav.domain.model

sealed class NavigationState {
    data object Idle : NavigationState()
    data object Calculating : NavigationState()

    data class Active(
        val route: Route,
        val currentLocation: Location,
        val snappedLocation: Location,
        val currentStepIndex: Int,
        val currentInstruction: RouteInstruction,
        val nextInstruction: RouteInstruction?,
        val distanceToNextTurnMeters: Double,
        val remainingDistanceMeters: Double,
        val remainingDurationSeconds: Long,
        val currentSpeedKmh: Float,
        val currentRoadName: String,
        val progressFraction: Float,
        val isOffRoute: Boolean = false
    ) : NavigationState()

    data class Rerouting(
        val reason: String = "Recalculating route..."
    ) : NavigationState()

    data class Arrived(
        val finalDestination: Location,
        val totalDistanceMeters: Double,
        val totalDurationSeconds: Long,
        val averageSpeedKmh: Float
    ) : NavigationState()
}
