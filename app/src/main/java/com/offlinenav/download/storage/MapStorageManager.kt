package com.offlinenav.download.storage

import android.content.Context
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getMapsDirectory(): File {
        val dir = File(context.getExternalFilesDir(null), "maps")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getRegionDirectory(regionId: String): File {
        val dir = File(getMapsDirectory(), regionId)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getVectorTilesFile(regionId: String): File = File(getRegionDirectory(regionId), "tiles.mbtiles")

    fun getSearchDbFile(regionId: String): File = File(getRegionDirectory(regionId), "search.db")

    fun getRoutingDirectory(regionId: String): File {
        val dir = File(getRegionDirectory(regionId), "valhalla_tiles")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun isRegionDownloaded(regionId: String): Boolean {
        val regionDir = File(getMapsDirectory(), regionId)
        return regionDir.exists() && regionDir.listFiles()?.isNotEmpty() == true
    }

    fun deleteRegion(regionId: String): Boolean {
        val regionDir = File(getMapsDirectory(), regionId)
        return if (regionDir.exists()) regionDir.deleteRecursively() else false
    }

    fun getAvailableDiskSpaceBytes(): Long {
        val stat = StatFs(Environment.getDataDirectory().path)
        return stat.availableBytes
    }

    fun getUsedMapStorageBytes(): Long {
        return getMapsDirectory().walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }
}
