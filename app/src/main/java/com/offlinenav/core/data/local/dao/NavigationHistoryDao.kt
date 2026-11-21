package com.offlinenav.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.offlinenav.core.data.local.entity.NavigationHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NavigationHistoryEntity] — past navigation sessions.
 */
@Dao
interface NavigationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: NavigationHistoryEntity): Long

    @Update
    suspend fun update(history: NavigationHistoryEntity)

    @Delete
    suspend fun delete(history: NavigationHistoryEntity)

    @Query("DELETE FROM navigation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM navigation_history")
    suspend fun clearAll()

    @Query("""
        SELECT * FROM navigation_history 
        ORDER BY started_at DESC 
        LIMIT :limit
    """)
    fun getRecentHistory(limit: Int = 50): Flow<List<NavigationHistoryEntity>>

    @Query("SELECT * FROM navigation_history WHERE id = :id")
    suspend fun getHistoryById(id: Long): NavigationHistoryEntity?

    @Query("""
        SELECT * FROM navigation_history 
        WHERE destination_name LIKE '%' || :query || '%'
        ORDER BY started_at DESC 
        LIMIT :limit
    """)
    suspend fun searchHistory(query: String, limit: Int = 20): List<NavigationHistoryEntity>

    @Query("SELECT COUNT(*) FROM navigation_history")
    fun getHistoryCount(): Flow<Int>

    @Query("""
        SELECT * FROM navigation_history 
        WHERE started_at >= :fromTimestamp 
        ORDER BY started_at DESC
    """)
    fun getHistorySince(fromTimestamp: Long): Flow<List<NavigationHistoryEntity>>

    @Query("""
        UPDATE navigation_history 
        SET is_completed = 1, completed_at = :completedAt 
        WHERE id = :id
    """)
    suspend fun markCompleted(id: Long, completedAt: Long = System.currentTimeMillis())
}
