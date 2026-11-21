package com.offlinenav.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.offlinenav.core.data.local.entity.SavedPlaceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [SavedPlaceEntity] — user's favourite / saved locations.
 */
@Dao
interface SavedPlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: SavedPlaceEntity): Long

    @Update
    suspend fun update(place: SavedPlaceEntity)

    @Delete
    suspend fun delete(place: SavedPlaceEntity)

    @Query("DELETE FROM saved_places WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM saved_places ORDER BY last_visited_at DESC, created_at DESC")
    fun getAllPlaces(): Flow<List<SavedPlaceEntity>>

    @Query("SELECT * FROM saved_places WHERE id = :id")
    suspend fun getPlaceById(id: Long): SavedPlaceEntity?

    @Query("SELECT * FROM saved_places WHERE category = :category ORDER BY visit_count DESC")
    fun getPlacesByCategory(category: String): Flow<List<SavedPlaceEntity>>

    @Query("""
        SELECT * FROM saved_places 
        WHERE name LIKE '%' || :query || '%' 
        OR address LIKE '%' || :query || '%'
        ORDER BY visit_count DESC
        LIMIT :limit
    """)
    suspend fun searchPlaces(query: String, limit: Int = 20): List<SavedPlaceEntity>

    @Query("SELECT COUNT(*) FROM saved_places")
    fun getPlaceCount(): Flow<Int>

    @Query("""
        UPDATE saved_places 
        SET visit_count = visit_count + 1, last_visited_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun incrementVisitCount(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM saved_places WHERE category IN ('home', 'work') ORDER BY category")
    fun getQuickAccessPlaces(): Flow<List<SavedPlaceEntity>>
}
