package com.offlinenav.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteInstruction(
    val stepIndex: Int,
    val instruction: String,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val turnType: TurnType,
    val roadName: String = "",
    val latitude: Double,
    val longitude: Double,
    val bearing: Float = 0f,
    val exitNumber: Int? = null
) : Parcelable
