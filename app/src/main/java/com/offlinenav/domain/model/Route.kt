package com.offlinenav.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(
    val id: String,
    val origin: Location,
    val destination: Location,
    val points: List<Location>,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val instructions: List<RouteInstruction>,
    val travelMode: TravelMode,
    val routeType: RouteType,
    val isAlternative: Boolean = false,
    val hasTolls: Boolean = false,
    val hasHighways: Boolean = false,
    val polylineEncoded: String = ""
) : Parcelable {
    val distanceFormatted: String
        get() = DistanceUnit.KILOMETERS.format(distanceMeters)

    val durationFormatted: String
        get() {
            val mins = (durationSeconds / 60).toInt()
            val hours = mins / 60
            val remMins = mins % 60
            return if (hours > 0) "${hours}h ${remMins}m" else "${remMins} min"
        }

    val etaFormatted: String
        get() {
            val etaMillis = System.currentTimeMillis() + (durationSeconds * 1000)
            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            return format.format(java.util.Date(etaMillis))
        }
}
