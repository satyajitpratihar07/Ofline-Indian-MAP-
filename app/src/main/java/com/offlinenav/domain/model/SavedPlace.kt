package com.offlinenav.domain.model

enum class PlaceCategory {
    HOME,
    WORK,
    FAVORITE,
    RECENT,
    CUSTOM
}

data class SavedPlace(
    val id: Long = 0L,
    val name: String,
    val address: String,
    val category: PlaceCategory,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long = System.currentTimeMillis()
)
