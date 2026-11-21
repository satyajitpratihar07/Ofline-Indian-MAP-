package com.offlinenav.gps.speed

import android.location.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeedCalculator @Inject constructor() {

    private val speedHistory = ArrayDeque<Float>(5)
    private var previousLocation: Location? = null

    fun calculateSpeedKmh(currentLocation: Location): Float {
        val rawSpeedKmh = if (currentLocation.hasSpeed() && currentLocation.speed > 0f) {
            currentLocation.speed * 3.6f // m/s to km/h
        } else {
            // Fallback: calculate delta distance / delta time
            val prev = previousLocation
            if (prev != null) {
                val distMeters = currentLocation.distanceTo(prev)
                val timeDeltaSeconds = (currentLocation.time - prev.time) / 1000.0f
                if (timeDeltaSeconds in 0.5f..10.0f) {
                    (distMeters / timeDeltaSeconds) * 3.6f
                } else 0f
            } else 0f
        }

        previousLocation = currentLocation

        // Discard stationary GPS jitter (under 1.5 km/h is considered stationary)
        val filtered = if (rawSpeedKmh < 1.5f) 0f else rawSpeedKmh

        // 5-point moving average filter to smooth speedometer needle
        if (speedHistory.size >= 5) {
            speedHistory.removeFirst()
        }
        speedHistory.addLast(filtered)

        return speedHistory.average().toFloat()
    }

    fun reset() {
        speedHistory.clear()
        previousLocation = null
    }
}
