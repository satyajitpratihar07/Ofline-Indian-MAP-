package com.offlinenav.gps.speed

import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.TripStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripStatisticsTracker @Inject constructor() {

    private val _tripStats = MutableStateFlow(TripStats())
    val tripStats: StateFlow<TripStats> = _tripStats.asStateFlow()

    private var previousLocation: Location? = null
    private var lastUpdateTime = System.currentTimeMillis()

    fun updateWithLocation(location: Location) {
        val current = _tripStats.value
        val now = System.currentTimeMillis()
        val dtSeconds = ((now - lastUpdateTime) / 1000L).coerceAtLeast(0L)
        lastUpdateTime = now

        val prev = previousLocation
        val deltaDistanceMeters = if (prev != null) {
            prev.distanceTo(location)
        } else 0.0

        previousLocation = location

        val speed = location.speedKmh
        val isMoving = speed > 2.0f

        val newMovingDuration = current.movingDurationSeconds + (if (isMoving) dtSeconds else 0L)
        val newStationaryDuration = current.stationaryDurationSeconds + (if (!isMoving) dtSeconds else 0L)
        val newTotalDistance = current.totalDistanceMeters + deltaDistanceMeters
        val newMaxSpeed = maxOf(current.maxSpeedKmh, speed)

        val newAvgSpeed = if (newMovingDuration > 0) {
            ((newTotalDistance / newMovingDuration) * 3.6).toFloat()
        } else 0f

        _tripStats.value = TripStats(
            currentSpeedKmh = speed,
            averageSpeedKmh = newAvgSpeed,
            maxSpeedKmh = newMaxSpeed,
            totalDistanceMeters = newTotalDistance,
            movingDurationSeconds = newMovingDuration,
            stationaryDurationSeconds = newStationaryDuration,
            tripStartTime = current.tripStartTime
        )
    }

    fun reset() {
        previousLocation = null
        lastUpdateTime = System.currentTimeMillis()
        _tripStats.value = TripStats(tripStartTime = lastUpdateTime)
    }
}
