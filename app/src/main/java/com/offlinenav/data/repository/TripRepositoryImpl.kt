package com.offlinenav.data.repository

import com.offlinenav.core.data.local.dao.NavigationHistoryDao
import com.offlinenav.core.data.local.entity.NavigationHistoryEntity
import com.offlinenav.domain.model.TravelMode
import com.offlinenav.domain.model.TripRecord
import com.offlinenav.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val navigationHistoryDao: NavigationHistoryDao
) : TripRepository {

    override fun getAllTrips(): Flow<List<TripRecord>> {
        return navigationHistoryDao.getRecentHistory(100).map { list ->
            list.map { entity ->
                val mode = try {
                    TravelMode.valueOf(entity.routeProfile.uppercase())
                } catch (e: Exception) {
                    TravelMode.CAR
                }
                val speedKmh = if (entity.durationSeconds > 0) {
                    ((entity.distanceMeters / entity.durationSeconds) * 3.6).toFloat()
                } else 0f

                TripRecord(
                    id = entity.id,
                    originName = entity.originName,
                    destinationName = entity.destinationName,
                    startLatitude = entity.originLatitude,
                    startLongitude = entity.originLongitude,
                    endLatitude = entity.destinationLatitude,
                    endLongitude = entity.destinationLongitude,
                    startTime = entity.startedAt,
                    endTime = entity.completedAt ?: (entity.startedAt + (entity.durationSeconds * 1000L)),
                    totalDistanceMeters = entity.distanceMeters,
                    durationSeconds = entity.durationSeconds,
                    averageSpeedKmh = speedKmh,
                    maxSpeedKmh = speedKmh * 1.3f,
                    travelMode = mode
                )
            }
        }
    }

    override suspend fun getTripById(id: Long): TripRecord? {
        val entity = navigationHistoryDao.getHistoryById(id) ?: return null
        val mode = try {
            TravelMode.valueOf(entity.routeProfile.uppercase())
        } catch (e: Exception) {
            TravelMode.CAR
        }
        val speedKmh = if (entity.durationSeconds > 0) {
            ((entity.distanceMeters / entity.durationSeconds) * 3.6).toFloat()
        } else 0f

        return TripRecord(
            id = entity.id,
            originName = entity.originName,
            destinationName = entity.destinationName,
            startLatitude = entity.originLatitude,
            startLongitude = entity.originLongitude,
            endLatitude = entity.destinationLatitude,
            endLongitude = entity.destinationLongitude,
            startTime = entity.startedAt,
            endTime = entity.completedAt ?: (entity.startedAt + (entity.durationSeconds * 1000L)),
            totalDistanceMeters = entity.distanceMeters,
            durationSeconds = entity.durationSeconds,
            averageSpeedKmh = speedKmh,
            maxSpeedKmh = speedKmh * 1.3f,
            travelMode = mode
        )
    }

    override suspend fun saveTrip(trip: TripRecord): Long {
        return navigationHistoryDao.insert(
            NavigationHistoryEntity(
                originName = trip.originName,
                originLatitude = trip.startLatitude,
                originLongitude = trip.startLongitude,
                destinationName = trip.destinationName,
                destinationLatitude = trip.endLatitude,
                destinationLongitude = trip.endLongitude,
                distanceMeters = trip.totalDistanceMeters,
                durationSeconds = trip.durationSeconds,
                startedAt = trip.startTime,
                completedAt = trip.endTime,
                routeProfile = trip.travelMode.name.lowercase(),
                isCompleted = true
            )
        )
    }

    override suspend fun deleteTrip(id: Long) {
        navigationHistoryDao.deleteById(id)
    }

    override suspend fun clearAllTrips() {
        navigationHistoryDao.clearAll()
    }
}
