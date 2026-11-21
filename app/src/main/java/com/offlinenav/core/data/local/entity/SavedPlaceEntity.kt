package com.offlinenav.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a user-saved / favourite place.
 */
@Entity(
    tableName = "saved_places",
    indices = [
        Index(value = ["latitude", "longitude"]),
        Index(value = ["name"]),
    ]
)
data class SavedPlaceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "category")
    val category: String = "general",   // home, work, general, favourite

    @ColumnInfo(name = "icon_name")
    val iconName: String = "place",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_visited_at")
    val lastVisitedAt: Long? = null,

    @ColumnInfo(name = "visit_count")
    val visitCount: Int = 0,

    @ColumnInfo(name = "notes")
    val notes: String? = null,
)
