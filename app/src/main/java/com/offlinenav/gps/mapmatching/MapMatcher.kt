package com.offlinenav.gps.mapmatching

import com.offlinenav.domain.model.Location
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

data class SnappedPoint(
    val snappedLocation: Location,
    val distanceToRouteMeters: Double,
    val segmentIndex: Int,
    val progressAlongSegment: Double
)

@Singleton
class MapMatcher @Inject constructor() {

    /**
     * Snaps a GPS coordinate onto the nearest segment of the planned route polyline.
     * Uses perpendicular projection onto line segments.
     */
    fun snapToRoute(
        gpsLocation: Location,
        routePoints: List<Location>,
        maxSnapDistanceMeters: Double = 50.0
    ): SnappedPoint? {
        if (routePoints.size < 2) return null

        var minDistance = Double.MAX_VALUE
        var bestSnappedLocation: Location? = null
        var bestSegmentIndex = 0
        var bestProgress = 0.0

        for (i in 0 until routePoints.size - 1) {
            val p1 = routePoints[i]
            val p2 = routePoints[i + 1]

            val snapped = projectPointOnSegment(gpsLocation, p1, p2)
            val dist = gpsLocation.distanceTo(snapped.first)

            if (dist < minDistance) {
                minDistance = dist
                bestSnappedLocation = snapped.first
                bestSegmentIndex = i
                bestProgress = snapped.second
            }
        }

        return if (bestSnappedLocation != null) {
            // Retain original altitude & speed from GPS, but snap lat/lng and interpolate bearing
            val snappedWithMeta = bestSnappedLocation.copy(
                altitude = gpsLocation.altitude,
                speedKmh = gpsLocation.speedKmh,
                bearing = if (bestSegmentIndex < routePoints.size - 1) {
                    routePoints[bestSegmentIndex].bearingTo(routePoints[bestSegmentIndex + 1])
                } else gpsLocation.bearing
            )
            SnappedPoint(snappedWithMeta, minDistance, bestSegmentIndex, bestProgress)
        } else null
    }

    private fun projectPointOnSegment(
        p: Location,
        a: Location,
        b: Location
    ): Pair<Location, Double> {
        val dx = b.longitude - a.longitude
        val dy = b.latitude - a.latitude
        val segLengthSq = dx * dx + dy * dy

        if (segLengthSq == 0.0) return Pair(a, 0.0)

        // Projection fraction t along AB
        val t = ((p.longitude - a.longitude) * dx + (p.latitude - a.latitude) * dy) / segLengthSq
        val clampedT = t.coerceIn(0.0, 1.0)

        val snappedLat = a.latitude + clampedT * dy
        val snappedLng = a.longitude + clampedT * dx

        return Pair(
            Location(latitude = snappedLat, longitude = snappedLng),
            clampedT
        )
    }
}
