package com.offlinenav.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResult(
    val id: Long,
    val name: String,
    val address: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Double = 0.0,
    val osmType: String = "node",
    val osmId: Long = 0L,
    val isSaved: Boolean = false
) : Parcelable {
    val distanceFormatted: String
        get() = DistanceUnit.KILOMETERS.format(distanceMeters)
}
