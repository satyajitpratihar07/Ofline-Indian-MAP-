package com.offlinenav.gps.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.offlinenav.domain.model.Location
import com.offlinenav.domain.repository.LocationRepository
import com.offlinenav.gps.speed.SpeedCalculator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speedCalculator: SpeedCalculator
) : LocationRepository {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var lastValidLocation: Location? = null

    override fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMs: Long): Flow<Location> = callbackFlow {
        if (!LocationPermissionHelper.hasLocationPermission(context)) {
            // Gracefully close flow without crashing when permission is not yet granted
            close()
            return@callbackFlow
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(androidLoc: android.location.Location) {
                // Reject extremely low-accuracy readings (e.g. cell tower accuracy > 100m)
                if (androidLoc.hasAccuracy() && androidLoc.accuracy > 70f) {
                    return
                }

                val speedKmh = speedCalculator.calculateSpeedKmh(androidLoc)
                val bearing = if (androidLoc.hasBearing()) androidLoc.bearing else {
                    lastValidLocation?.bearingTo(
                        Location(androidLoc.latitude, androidLoc.longitude)
                    ) ?: 0f
                }

                val loc = Location(
                    latitude = androidLoc.latitude,
                    longitude = androidLoc.longitude,
                    altitude = if (androidLoc.hasAltitude()) androidLoc.altitude else 0.0,
                    bearing = bearing,
                    speedKmh = speedKmh,
                    accuracyMeters = if (androidLoc.hasAccuracy()) androidLoc.accuracy else 10f,
                    timestamp = androidLoc.time
                )

                lastValidLocation = loc
                trySend(loc)
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Register with GPS_PROVIDER first for pinpoint offline accuracy
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                intervalMs,
                1.0f,
                listener,
                Looper.getMainLooper()
            )
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                intervalMs,
                5.0f,
                listener,
                Looper.getMainLooper()
            )
        }

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        if (!LocationPermissionHelper.hasLocationPermission(context)) return null

        val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        val best = when {
            gpsLoc != null && netLoc != null -> if (gpsLoc.time > netLoc.time) gpsLoc else netLoc
            gpsLoc != null -> gpsLoc
            else -> netLoc
        } ?: return lastValidLocation

        return Location(
            latitude = best.latitude,
            longitude = best.longitude,
            altitude = best.altitude,
            bearing = best.bearing,
            speedKmh = best.speed * 3.6f,
            accuracyMeters = best.accuracy,
            timestamp = best.time
        ).also { lastValidLocation = it }
    }
}
