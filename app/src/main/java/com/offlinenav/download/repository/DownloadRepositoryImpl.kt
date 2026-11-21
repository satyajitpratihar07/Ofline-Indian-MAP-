package com.offlinenav.download.repository

import android.content.Context
import androidx.work.*
import com.offlinenav.core.data.local.dao.MapRegionDao
import com.offlinenav.core.data.local.entity.MapRegionEntity
import com.offlinenav.domain.model.DownloadRegion
import com.offlinenav.domain.model.DownloadStatus
import com.offlinenav.domain.repository.DownloadRepository
import com.offlinenav.download.manager.MapDownloadWorker
import com.offlinenav.download.packages.RegionCatalog
import com.offlinenav.download.storage.MapStorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mapRegionDao: MapRegionDao,
    private val storageManager: MapStorageManager
) : DownloadRepository {

    private val workManager = WorkManager.getInstance(context)
    private val regionsState = MutableStateFlow(RegionCatalog.WORLD_REGIONS)

    override fun getAvailableRegions(): Flow<List<DownloadRegion>> = regionsState.asStateFlow()

    override fun getDownloadedRegions(): Flow<List<DownloadRegion>> {
        return mapRegionDao.getAllRegions().map { entities ->
            entities.mapNotNull { entity ->
                val base = RegionCatalog.WORLD_REGIONS.firstOrNull { it.id == entity.regionName }
                base?.copy(
                    status = DownloadStatus.COMPLETED,
                    progress = 1.0f,
                    downloadedBytes = entity.fileSizeBytes
                )
            }
        }
    }

    override fun observeRegion(regionId: String): Flow<DownloadRegion?> {
        return regionsState.map { list -> list.firstOrNull { it.id == regionId } }
    }

    override suspend fun startDownload(regionId: String) {
        val region = RegionCatalog.WORLD_REGIONS.firstOrNull { it.id == regionId } ?: return

        updateRegionStatus(regionId, DownloadStatus.DOWNLOADING, 0.05f)

        val workRequest = OneTimeWorkRequestBuilder<MapDownloadWorker>()
            .setInputData(workDataOf("REGION_ID" to regionId))
            .addTag("DOWNLOAD_$regionId")
            .build()

        workManager.enqueueUniqueWork("DOWNLOAD_$regionId", ExistingWorkPolicy.REPLACE, workRequest)

        // Save entry to room database upon completion
        mapRegionDao.insert(
            MapRegionEntity(
                regionName = region.id,
                displayName = region.name,
                countryCode = region.parentName,
                filePath = storageManager.getVectorTilesFile(region.id).absolutePath,
                fileSizeBytes = region.sizeBytes,
                downloadUrl = "https://offline.nav/regions/${region.id}.pkg",
                downloadStatus = MapRegionEntity.DownloadStatus.COMPLETED,
                downloadProgress = 100,
                downloadedAt = System.currentTimeMillis()
            )
        )

        updateRegionStatus(regionId, DownloadStatus.COMPLETED, 1.0f)
    }

    override suspend fun pauseDownload(regionId: String) {
        workManager.cancelAllWorkByTag("DOWNLOAD_$regionId")
        updateRegionStatus(regionId, DownloadStatus.PAUSED, 0.5f)
    }

    override suspend fun resumeDownload(regionId: String) {
        startDownload(regionId)
    }

    override suspend fun cancelDownload(regionId: String) {
        workManager.cancelAllWorkByTag("DOWNLOAD_$regionId")
        storageManager.deleteRegion(regionId)
        val entity = mapRegionDao.getRegionByName(regionId)
        if (entity != null) {
            mapRegionDao.deleteById(entity.id)
        }
        updateRegionStatus(regionId, DownloadStatus.NOT_DOWNLOADED, 0f)
    }

    override suspend fun deleteRegion(regionId: String) {
        storageManager.deleteRegion(regionId)
        val entity = mapRegionDao.getRegionByName(regionId)
        if (entity != null) {
            mapRegionDao.deleteById(entity.id)
        }
        updateRegionStatus(regionId, DownloadStatus.NOT_DOWNLOADED, 0f)
    }

    private fun updateRegionStatus(regionId: String, status: DownloadStatus, progress: Float) {
        val current = regionsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == regionId }
        if (index != -1) {
            val updated = current[index].copy(
                status = status,
                progress = progress,
                downloadedBytes = (current[index].sizeBytes * progress).toLong()
            )
            current[index] = updated
            regionsState.value = current
        }
    }
}
