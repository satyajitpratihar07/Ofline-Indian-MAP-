package com.offlinenav.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val latitude: Double,
    val longitude: Double,
    val name: String = "",
    val address: String = "",
    val altitude: Double = 0.0,
    val bearing: Float = 0f,
    val speedKmh: Float = 0f,
    val accuracyMeters: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    fun hasBearing(): Boolean = bearing != 0f
    fun hasSpeed(): Boolean = speedKmh > 0f
    fun isAccurate(maxAccuracyMeters: Float = 30f): Boolean = accuracyMeters in 0.1f..maxAccuracyMeters

    fun distanceTo(other: Location): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLng = Math.toRadians(other.longitude - longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    fun bearingTo(other: Location): Float {
        val lat1 = Math.toRadians(latitude)
        val lon1 = Math.toRadians(longitude)
        val lat2 = Math.toRadians(other.latitude)
        val lon2 = Math.toRadians(other.longitude)
        val dLon = lon2 - lon1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        val rad = Math.atan2(y, x)
        return ((Math.toDegrees(rad) + 360) % 360).toFloat()
    }
}
