package com.offlinenav.domain.model

data class TripStats(
    val currentSpeedKmh: Float = 0f,
    val averageSpeedKmh: Float = 0f,
    val maxSpeedKmh: Float = 0f,
    val totalDistanceMeters: Double = 0.0,
    val movingDurationSeconds: Long = 0L,
    val stationaryDurationSeconds: Long = 0L,
    val tripStartTime: Long = System.currentTimeMillis()
) {
    val totalDurationSeconds: Long
        get() = movingDurationSeconds + stationaryDurationSeconds
}
