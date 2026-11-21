package com.offlinenav.domain.model

enum class RegionType {
    CONTINENT,
    COUNTRY,
    STATE,
    CITY
}

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    UPDATE_AVAILABLE
}

data class DownloadRegion(
    val id: String,
    val name: String,
    val parentName: String = "",
    val type: RegionType,
    val minLat: Double,
    val minLng: Double,
    val maxLat: Double,
    val maxLng: Double,
    val sizeBytes: Long,
    val downloadedBytes: Long = 0L,
    val status: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
    val progress: Float = 0f,
    val downloadSpeedBytesPerSec: Long = 0L,
    val mapVersion: String = "2024.1",
    val hasRouting: Boolean = true,
    val hasSearch: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val sizeFormatted: String
        get() = String.format("%.1f MB", sizeBytes / (1024.0 * 1024.0))

    val progressPercent: Int
        get() = (progress * 100).toInt()
}
