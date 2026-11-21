package com.offlinenav.presentation.map

import android.content.Context
import com.offlinenav.R
import com.offlinenav.download.storage.MapStorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineMapManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storageManager: MapStorageManager
) {

    fun getMapStyleJson(isDarkMode: Boolean): String {
        val resId = if (isDarkMode) R.raw.map_style_dark else R.raw.map_style_light
        return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }

    fun hasDownloadedMaps(): Boolean {
        return storageManager.getUsedMapStorageBytes() > 0
    }
}
