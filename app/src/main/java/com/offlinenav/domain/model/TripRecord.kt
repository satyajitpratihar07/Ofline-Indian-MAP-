package com.offlinenav.domain.model

data class TripRecord(
    val id: Long = 0L,
    val originName: String,
    val destinationName: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val startTime: Long,
    val endTime: Long,
    val totalDistanceMeters: Double,
    val durationSeconds: Long,
    val averageSpeedKmh: Float,
    val maxSpeedKmh: Float,
    val travelMode: TravelMode,
    val pointsJson: String = ""
)
