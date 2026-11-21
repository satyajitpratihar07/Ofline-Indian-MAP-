package com.offlinenav.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a downloaded offline map region.
 */
@Entity(
    tableName = "map_regions",
    indices = [
        Index(value = ["region_name"], unique = true),
        Index(value = ["download_status"]),
    ]
)
data class MapRegionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "region_name")
    val regionName: String,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "country_code")
    val countryCode: String,

    @ColumnInfo(name = "file_path")
    val filePath: String? = null,

    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long = 0L,

    @ColumnInfo(name = "download_url")
    val downloadUrl: String,

    @ColumnInfo(name = "download_status")
    val downloadStatus: String = DownloadStatus.NOT_STARTED,  // enum-like string

    @ColumnInfo(name = "download_progress")
    val downloadProgress: Int = 0,   // 0–100

    @ColumnInfo(name = "downloaded_at")
    val downloadedAt: Long? = null,

    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long? = null,

    @ColumnInfo(name = "bounding_box_json")
    val boundingBoxJson: String? = null,

    @ColumnInfo(name = "version")
    val version: String = "1",

    @ColumnInfo(name = "tile_count")
    val tileCount: Long = 0L,
) {
    object DownloadStatus {
        const val NOT_STARTED  = "not_started"
        const val QUEUED       = "queued"
        const val DOWNLOADING  = "downloading"
        const val PAUSED       = "paused"
        const val COMPLETED    = "completed"
        const val FAILED       = "failed"
        const val EXTRACTING   = "extracting"
    }
}
