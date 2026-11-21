package com.offlinenav.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.offlinenav.core.data.local.converter.DateConverter
import com.offlinenav.core.data.local.converter.ListConverter
import com.offlinenav.core.data.local.dao.MapRegionDao
import com.offlinenav.core.data.local.dao.NavigationHistoryDao
import com.offlinenav.core.data.local.dao.SavedPlaceDao
import com.offlinenav.core.data.local.entity.MapRegionEntity
import com.offlinenav.core.data.local.entity.NavigationHistoryEntity
import com.offlinenav.core.data.local.entity.SavedPlaceEntity

/**
 * Room database for OfflineNav.
 *
 * Contains tables for:
 * - [SavedPlaceEntity]: User's favourite / saved locations
 * - [NavigationHistoryEntity]: Past navigation sessions
 * - [MapRegionEntity]: Downloaded offline map regions
 */
@Database(
    entities = [
        SavedPlaceEntity::class,
        NavigationHistoryEntity::class,
        MapRegionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class OfflineNavDatabase : RoomDatabase() {

    abstract fun savedPlaceDao(): SavedPlaceDao
    abstract fun navigationHistoryDao(): NavigationHistoryDao
    abstract fun mapRegionDao(): MapRegionDao

    companion object {
        const val DATABASE_NAME = "offlinenav.db"

        /** Array of Room migrations to pass to databaseBuilder */
        val MIGRATIONS: Array<Migration> = arrayOf(
            // Example: MIGRATION_1_2
            // Add migrations here as the schema evolves
        )
    }
}
