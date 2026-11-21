package com.offlinenav.domain.repository

import com.offlinenav.domain.model.TripRecord
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<TripRecord>>
    suspend fun getTripById(id: Long): TripRecord?
    suspend fun saveTrip(trip: TripRecord): Long
    suspend fun deleteTrip(id: Long)
    suspend fun clearAllTrips()
}
