package com.offlinenav.core.data.local.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room TypeConverter for [Date] ↔ [Long] (epoch milliseconds).
 */
class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
