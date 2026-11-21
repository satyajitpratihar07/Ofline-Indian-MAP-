package com.offlinenav.domain.repository

import com.offlinenav.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(intervalMs: Long = 1000L): Flow<Location>
    suspend fun getLastKnownLocation(): Location?
    fun isLocationEnabled(): Boolean
}
