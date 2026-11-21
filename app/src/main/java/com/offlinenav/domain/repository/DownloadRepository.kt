package com.offlinenav.domain.repository

import com.offlinenav.domain.model.DownloadRegion
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAvailableRegions(): Flow<List<DownloadRegion>>
    fun getDownloadedRegions(): Flow<List<DownloadRegion>>
    fun observeRegion(regionId: String): Flow<DownloadRegion?>
    suspend fun startDownload(regionId: String)
    suspend fun pauseDownload(regionId: String)
    suspend fun resumeDownload(regionId: String)
    suspend fun cancelDownload(regionId: String)
    suspend fun deleteRegion(regionId: String)
}
