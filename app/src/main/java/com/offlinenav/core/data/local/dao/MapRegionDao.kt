package com.offlinenav.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.offlinenav.core.data.local.entity.MapRegionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [MapRegionEntity] — offline map region management.
 */
@Dao
interface MapRegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(region: MapRegionEntity): Long

    @Update
    suspend fun update(region: MapRegionEntity)

    @Delete
    suspend fun delete(region: MapRegionEntity)

    @Query("DELETE FROM map_regions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM map_regions ORDER BY display_name ASC")
    fun getAllRegions(): Flow<List<MapRegionEntity>>

    @Query("SELECT * FROM map_regions WHERE id = :id")
    suspend fun getRegionById(id: Long): MapRegionEntity?

    @Query("SELECT * FROM map_regions WHERE region_name = :regionName")
    suspend fun getRegionByName(regionName: String): MapRegionEntity?

    @Query("SELECT * FROM map_regions WHERE download_status = :status")
    fun getRegionsByStatus(status: String): Flow<List<MapRegionEntity>>

    @Query("SELECT * FROM map_regions WHERE download_status = 'completed' ORDER BY display_name")
    fun getDownloadedRegions(): Flow<List<MapRegionEntity>>

    @Query("SELECT * FROM map_regions WHERE download_status IN ('downloading', 'queued', 'paused')")
    fun getActiveDownloads(): Flow<List<MapRegionEntity>>

    @Query("SELECT * FROM map_regions WHERE country_code = :countryCode ORDER BY display_name")
    fun getRegionsByCountry(countryCode: String): Flow<List<MapRegionEntity>>

    @Query("""
        UPDATE map_regions 
        SET download_status = :status, download_progress = :progress 
        WHERE id = :id
    """)
    suspend fun updateDownloadProgress(id: Long, status: String, progress: Int)

    @Query("""
        UPDATE map_regions 
        SET download_status = 'completed', download_progress = 100, 
            downloaded_at = :downloadedAt, file_path = :filePath, file_size_bytes = :fileSizeBytes 
        WHERE id = :id
    """)
    suspend fun markDownloadComplete(
        id: Long,
        filePath: String,
        fileSizeBytes: Long,
        downloadedAt: Long = System.currentTimeMillis()
    )

    @Query("SELECT SUM(file_size_bytes) FROM map_regions WHERE download_status = 'completed'")
    fun getTotalStorageUsedBytes(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM map_regions WHERE download_status = 'completed'")
    fun getDownloadedRegionCount(): Flow<Int>
}
