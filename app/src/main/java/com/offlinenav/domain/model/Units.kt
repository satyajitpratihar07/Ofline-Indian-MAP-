package com.offlinenav.domain.model

enum class SpeedUnit(val symbol: String) {
    KM_H("km/h"),
    MPH("mph");

    fun convertFromKmh(kmh: Float): Float = when (this) {
        KM_H -> kmh
        MPH -> kmh * 0.621371f
    }
}

enum class DistanceUnit(val symbol: String) {
    KILOMETERS("km"),
    MILES("mi");

    fun format(meters: Double): String {
        return when (this) {
            KILOMETERS -> {
                if (meters < 1000) {
                    "${meters.toInt()} m"
                } else {
                    String.format("%.1f km", meters / 1000.0)
                }
            }
            MILES -> {
                val miles = meters * 0.000621371
                if (miles < 0.1) {
                    "${(meters * 3.28084).toInt()} ft"
                } else {
                    String.format("%.1f mi", miles)
                }
            }
        }
    }
}
