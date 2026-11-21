package com.offlinenav.navigation.engine

import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.Route
import com.offlinenav.gps.mapmatching.MapMatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviationDetector @Inject constructor(
    private val mapMatcher: MapMatcher
) {
    private var consecutiveOffRouteCount = 0
    private var lastRerouteTime = 0L
    private val rerouteCooldownMs = 15_000L // 15 seconds cooldown

    fun isDeviated(currentGps: Location, activeRoute: Route): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastRerouteTime < rerouteCooldownMs) {
            return false
        }

        // Ignore inaccurate GPS readings while checking deviation
        if (currentGps.accuracyMeters > 35f) {
            return false
        }

        val snapped = mapMatcher.snapToRoute(currentGps, activeRoute.points)
        val distanceToRoute = snapped?.distanceToRouteMeters ?: Double.MAX_VALUE

        // 75m off-route threshold
        if (distanceToRoute > 75.0) {
            consecutiveOffRouteCount++
        } else {
            consecutiveOffRouteCount = 0
        }

        // Require 3 consecutive off-route fixes before triggering reroute
        val isDeviated = consecutiveOffRouteCount >= 3
        if (isDeviated) {
            lastRerouteTime = now
            consecutiveOffRouteCount = 0
        }
        return isDeviated
    }

    fun reset() {
        consecutiveOffRouteCount = 0
        lastRerouteTime = 0L
    }
}
