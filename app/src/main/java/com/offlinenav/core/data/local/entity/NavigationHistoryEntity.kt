package com.offlinenav.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a completed navigation session.
 */
@Entity(
    tableName = "navigation_history",
    indices = [Index(value = ["started_at"])]
)
data class NavigationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "origin_name")
    val originName: String,

    @ColumnInfo(name = "origin_latitude")
    val originLatitude: Double,

    @ColumnInfo(name = "origin_longitude")
    val originLongitude: Double,

    @ColumnInfo(name = "destination_name")
    val destinationName: String,

    @ColumnInfo(name = "destination_latitude")
    val destinationLatitude: Double,

    @ColumnInfo(name = "destination_longitude")
    val destinationLongitude: Double,

    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Double,

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long,

    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,

    @ColumnInfo(name = "route_profile")
    val routeProfile: String = "driving",   // driving, walking, cycling

    @ColumnInfo(name = "waypoints_json")
    val waypointsJson: String? = null,

    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
)
