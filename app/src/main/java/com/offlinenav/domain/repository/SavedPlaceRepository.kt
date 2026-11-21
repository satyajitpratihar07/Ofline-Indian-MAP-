package com.offlinenav.domain.repository

import com.offlinenav.domain.model.PlaceCategory
import com.offlinenav.domain.model.SavedPlace
import kotlinx.coroutines.flow.Flow

interface SavedPlaceRepository {
    fun getAllSavedPlaces(): Flow<List<SavedPlace>>
    suspend fun getPlaceByCategory(category: PlaceCategory): SavedPlace?
    suspend fun savePlace(place: SavedPlace): Long
    suspend fun deletePlace(id: Long)
}
