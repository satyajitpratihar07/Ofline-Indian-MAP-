package com.offlinenav.data.repository

import com.offlinenav.core.data.local.dao.SavedPlaceDao
import com.offlinenav.core.data.local.entity.SavedPlaceEntity
import com.offlinenav.domain.model.PlaceCategory
import com.offlinenav.domain.model.SavedPlace
import com.offlinenav.domain.repository.SavedPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedPlaceRepositoryImpl @Inject constructor(
    private val savedPlaceDao: SavedPlaceDao
) : SavedPlaceRepository {

    override fun getAllSavedPlaces(): Flow<List<SavedPlace>> {
        return savedPlaceDao.getAllPlaces().map { list ->
            list.map { entity ->
                SavedPlace(
                    id = entity.id,
                    name = entity.name,
                    address = entity.address,
                    category = when (entity.category.uppercase()) {
                        "HOME" -> PlaceCategory.HOME
                        "WORK" -> PlaceCategory.WORK
                        "FAVORITE" -> PlaceCategory.FAVORITE
                        else -> PlaceCategory.CUSTOM
                    },
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun getPlaceByCategory(category: PlaceCategory): SavedPlace? {
        val list = savedPlaceDao.getPlacesByCategory(category.name.lowercase()).firstOrNull()
        val entity = list?.firstOrNull() ?: return null
        return SavedPlace(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            category = category,
            latitude = entity.latitude,
            longitude = entity.longitude,
            createdAt = entity.createdAt
        )
    }

    override suspend fun savePlace(place: SavedPlace): Long {
        return savedPlaceDao.insert(
            SavedPlaceEntity(
                name = place.name,
                address = place.address,
                latitude = place.latitude,
                longitude = place.longitude,
                category = place.category.name.lowercase(),
                createdAt = place.createdAt
            )
        )
    }

    override suspend fun deletePlace(id: Long) {
        savedPlaceDao.deleteById(id)
    }
}
