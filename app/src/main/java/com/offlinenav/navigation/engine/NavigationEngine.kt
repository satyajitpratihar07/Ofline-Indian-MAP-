package com.offlinenav.navigation.engine

import com.offlinenav.domain.model.*
import com.offlinenav.domain.repository.LocationRepository
import com.offlinenav.domain.repository.RouteRepository
import com.offlinenav.gps.mapmatching.MapMatcher
import com.offlinenav.navigation.voice.NavigationTtsEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationEngine @Inject constructor(
    private val locationRepository: LocationRepository,
    private val routeRepository: RouteRepository,
    private val mapMatcher: MapMatcher,
    private val deviationDetector: DeviationDetector,
    private val ttsEngine: NavigationTtsEngine
) {
    private val engineScope = CoroutineScope(Dispatchers.Main + Job())
    private var trackingJob: Job? = null

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Idle)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private var activeRoute: Route? = null
    private var currentStepIndex = 0

    fun startNavigation(route: Route) {
        activeRoute = route
        currentStepIndex = 0
        deviationDetector.reset()

        val firstInstruction = route.instructions.firstOrNull() ?: RouteInstruction(
            0, "Follow the highlighted route", route.distanceMeters, route.durationSeconds,
            TurnType.DEPART, "", route.origin.latitude, route.origin.longitude
        )

        _navigationState.value = NavigationState.Active(
            route = route,
            currentLocation = route.origin,
            snappedLocation = route.origin,
            currentStepIndex = 0,
            currentInstruction = firstInstruction,
            nextInstruction = route.instructions.getOrNull(1),
            distanceToNextTurnMeters = firstInstruction.distanceMeters,
            remainingDistanceMeters = route.distanceMeters,
            remainingDurationSeconds = route.durationSeconds,
            currentSpeedKmh = 0f,
            currentRoadName = firstInstruction.roadName.ifEmpty { "Main Route" },
            progressFraction = 0f
        )

        ttsEngine.speak("Starting navigation. Head towards ${route.destination.name.ifEmpty { "destination" }}")

        trackingJob?.cancel()
        trackingJob = locationRepository.getLocationUpdates(1000L)
            .catch { e -> Timber.w(e, "Navigation engine location error") }
            .onEach { loc -> processLocationFix(loc) }
            .launchIn(engineScope)
    }

    private fun processLocationFix(gpsLocation: Location) {
        val route = activeRoute ?: return

        // 1. Check destination arrival (within 35 meters)
        val distToDestination = gpsLocation.distanceTo(route.destination)
        if (distToDestination <= 35.0) {
            handleArrival(route)
            return
        }

        // 2. Check route deviation and auto-reroute
        if (deviationDetector.isDeviated(gpsLocation, route)) {
            triggerReroute(gpsLocation, route)
            return
        }

        // 3. Snap coordinates to route polyline
        val snapped = mapMatcher.snapToRoute(gpsLocation, route.points)
        val snappedLocation = snapped?.snappedLocation ?: gpsLocation

        // 4. Update Turn-by-Turn Instruction Step
        val currentInstruction = route.instructions.getOrNull(currentStepIndex)
        val nextInstruction = route.instructions.getOrNull(currentStepIndex + 1)

        val distToTurn = if (currentInstruction != null) {
            val turnLoc = Location(currentInstruction.latitude, currentInstruction.longitude)
            snappedLocation.distanceTo(turnLoc)
        } else 0.0

        // Advance to next maneuver step if within 25m of current maneuver waypoint
        if (distToTurn <= 25.0 && currentStepIndex < route.instructions.size - 1) {
            currentStepIndex++
        }

        // Voice announcement
        currentInstruction?.let {
            ttsEngine.announceManeuver(it, distToTurn)
        }

        // 5. Update Remaining Distance & Progress
        val remainingDist = distToDestination.coerceAtLeast(0.0)
        val speedMps = (gpsLocation.speedKmh.coerceAtLeast(10f) * 1000.0) / 3600.0
        val remainingDuration = (remainingDist / speedMps).toLong()
        val progress = 1.0f - (remainingDist / route.distanceMeters).toFloat().coerceIn(0f, 1f)

        _navigationState.value = NavigationState.Active(
            route = route,
            currentLocation = gpsLocation,
            snappedLocation = snappedLocation,
            currentStepIndex = currentStepIndex,
            currentInstruction = route.instructions.getOrNull(currentStepIndex) ?: currentInstruction ?: return,
            nextInstruction = nextInstruction,
            distanceToNextTurnMeters = distToTurn,
            remainingDistanceMeters = remainingDist,
            remainingDurationSeconds = remainingDuration,
            currentSpeedKmh = gpsLocation.speedKmh,
            currentRoadName = currentInstruction?.roadName?.ifEmpty { "Highway" } ?: "Main Road",
            progressFraction = progress
        )
    }

    private fun triggerReroute(fromLocation: Location, oldRoute: Route) {
        _navigationState.value = NavigationState.Rerouting()
        ttsEngine.announceRerouting()

        engineScope.launch {
            val result = routeRepository.calculateRoutes(
                origin = fromLocation,
                destination = oldRoute.destination,
                travelMode = oldRoute.travelMode,
                routeType = oldRoute.routeType
            )

            if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
                val newRoute = result.getOrThrow().first()
                activeRoute = newRoute
                currentStepIndex = 0
                ttsEngine.speak("New route found. Continue straight")
            } else {
                // If reroute fails, resume with original route
                _navigationState.value = NavigationState.Active(
                    route = oldRoute,
                    currentLocation = fromLocation,
                    snappedLocation = fromLocation,
                    currentStepIndex = currentStepIndex,
                    currentInstruction = oldRoute.instructions.getOrNull(currentStepIndex) ?: oldRoute.instructions.first(),
                    nextInstruction = oldRoute.instructions.getOrNull(currentStepIndex + 1),
                    distanceToNextTurnMeters = 100.0,
                    remainingDistanceMeters = fromLocation.distanceTo(oldRoute.destination),
                    remainingDurationSeconds = 300,
                    currentSpeedKmh = fromLocation.speedKmh,
                    currentRoadName = "Rerouting...",
                    progressFraction = 0.5f,
                    isOffRoute = true
                )
            }
        }
    }

    private fun handleArrival(route: Route) {
        trackingJob?.cancel()
        ttsEngine.announceArrival(route.destination.name.ifEmpty { "destination" })
        _navigationState.value = NavigationState.Arrived(
            finalDestination = route.destination,
            totalDistanceMeters = route.distanceMeters,
            totalDurationSeconds = route.durationSeconds,
            averageSpeedKmh = route.travelMode.speedKmhAverage
        )
    }

    fun stopNavigation() {
        trackingJob?.cancel()
        ttsEngine.stop()
        activeRoute = null
        _navigationState.value = NavigationState.Idle
    }
}
