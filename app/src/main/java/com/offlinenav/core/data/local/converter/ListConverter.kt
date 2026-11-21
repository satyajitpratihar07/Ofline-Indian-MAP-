package com.offlinenav.core.data.local.converter

import androidx.room.TypeConverter

/**
 * Room TypeConverter for [List<String>] ↔ [String] (comma-separated).
 */
class ListConverter {

    @TypeConverter
    fun fromStringList(list: List<String>?): String? =
        list?.joinToString(separator = "|||")

    @TypeConverter
    fun toStringList(value: String?): List<String>? =
        value?.split("|||")?.filter { it.isNotBlank() }
}
