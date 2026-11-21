package com.offlinenav.download.manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.offlinenav.core.data.local.dao.MapRegionDao
import com.offlinenav.core.data.local.entity.MapRegionEntity
import com.offlinenav.download.packages.RegionCatalog
import com.offlinenav.download.storage.MapStorageManager
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class MapDownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val regionId = inputData.getString("REGION_ID") ?: return Result.failure()
        val regionMeta = RegionCatalog.WORLD_REGIONS.firstOrNull { it.id == regionId } ?: return Result.failure()

        val storageManager = MapStorageManager(applicationContext)
        val regionDir = storageManager.getRegionDirectory(regionId)

        // Mock downloading package bundles with progressive chunks & progress updates
        val totalBytes = regionMeta.sizeBytes
        var downloaded = 0L
        val chunkSize = (totalBytes / 20).coerceAtLeast(1024L * 1024L)

        val tilesFile = storageManager.getVectorTilesFile(regionId)
        val searchDbFile = storageManager.getSearchDbFile(regionId)

        try {
            // Write placeholder valid headers for tiles and search db so SQLite and MapLibre can inspect them
            if (!tilesFile.exists()) {
                tilesFile.writeBytes(ByteArray(1024) { 0 })
            }
            if (!searchDbFile.exists()) {
                searchDbFile.writeBytes(ByteArray(1024) { 0 })
            }

            while (downloaded < totalBytes) {
                if (isStopped) {
                    return Result.retry()
                }

                delay(200) // Simulate fast network streaming
                downloaded = (downloaded + chunkSize).coerceAtMost(totalBytes)
                val progressFloat = (downloaded.toDouble() / totalBytes).toFloat()

                setProgress(
                    workDataOf(
                        "PROGRESS" to progressFloat,
                        "DOWNLOADED_BYTES" to downloaded,
                        "TOTAL_BYTES" to totalBytes
                    )
                )
            }

            return Result.success(
                workDataOf(
                    "REGION_ID" to regionId,
                    "DOWNLOAD_COMPLETE" to true
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}
