package com.offlinenav.data.search

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.offlinenav.domain.model.SearchResult
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineSearchDatabase @Inject constructor(
    private val context: Context
) {

    /**
     * Executes an FTS5 full-text search against the regional search database.
     */
    fun search(dbFile: File, query: String, nearLat: Double, nearLng: Double, limit: Int = 30): List<SearchResult> {
        if (!dbFile.exists()) return emptyList()

        val results = mutableListOf<SearchResult>()
        var db: SQLiteDatabase? = null
        try {
            db = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)

            // FTS5 MATCH query with wildcard prefix matching
            val sanitized = query.trim().replace("'", "''") + "*"
            val sql = """
                SELECT p.id, p.name, p.address, p.category, p.lat, p.lng, p.osm_type, p.osm_id
                FROM places p
                JOIN places_fts ON places_fts.rowid = p.id
                WHERE places_fts MATCH ?
                LIMIT 100
            """.trimIndent()

            db.rawQuery(sql, arrayOf(sanitized)).use { cursor ->
                val idIdx = cursor.getColumnIndex("id")
                val nameIdx = cursor.getColumnIndex("name")
                val addrIdx = cursor.getColumnIndex("address")
                val catIdx = cursor.getColumnIndex("category")
                val latIdx = cursor.getColumnIndex("lat")
                val lngIdx = cursor.getColumnIndex("lng")
                val osmTypeIdx = cursor.getColumnIndex("osm_type")
                val osmIdIdx = cursor.getColumnIndex("osm_id")

                while (cursor.moveToNext()) {
                    val lat = cursor.getDouble(latIdx)
                    val lng = cursor.getDouble(lngIdx)
                    val dist = calculateDistanceMeters(nearLat, nearLng, lat, lng)

                    results.add(
                        SearchResult(
                            id = cursor.getLong(idIdx),
                            name = cursor.getString(nameIdx),
                            address = cursor.getString(addrIdx) ?: "",
                            category = cursor.getString(catIdx) ?: "place",
                            latitude = lat,
                            longitude = lng,
                            distanceMeters = dist,
                            osmType = cursor.getString(osmTypeIdx) ?: "node",
                            osmId = cursor.getLong(osmIdIdx)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.close()
        }

        // Rank by distance from user's current GPS location
        return results.sortedBy { it.distanceMeters }.take(limit)
    }

    /**
     * Searches places by specific category (hospital, fuel, restaurant, etc.)
     */
    fun searchByCategory(dbFile: File, category: String, nearLat: Double, nearLng: Double, limit: Int = 30): List<SearchResult> {
        if (!dbFile.exists()) return emptyList()

        val results = mutableListOf<SearchResult>()
        var db: SQLiteDatabase? = null
        try {
            db = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)

            val sql = "SELECT id, name, address, category, lat, lng, osm_type, osm_id FROM places WHERE category = ? LIMIT 100"
            db.rawQuery(sql, arrayOf(category.lowercase())).use { cursor ->
                val idIdx = cursor.getColumnIndex("id")
                val nameIdx = cursor.getColumnIndex("name")
                val addrIdx = cursor.getColumnIndex("address")
                val catIdx = cursor.getColumnIndex("category")
                val latIdx = cursor.getColumnIndex("lat")
                val lngIdx = cursor.getColumnIndex("lng")
                val osmTypeIdx = cursor.getColumnIndex("osm_type")
                val osmIdIdx = cursor.getColumnIndex("osm_id")

                while (cursor.moveToNext()) {
                    val lat = cursor.getDouble(latIdx)
                    val lng = cursor.getDouble(lngIdx)
                    val dist = calculateDistanceMeters(nearLat, nearLng, lat, lng)

                    results.add(
                        SearchResult(
                            id = cursor.getLong(idIdx),
                            name = cursor.getString(nameIdx),
                            address = cursor.getString(addrIdx) ?: "",
                            category = cursor.getString(catIdx) ?: category,
                            latitude = lat,
                            longitude = lng,
                            distanceMeters = dist,
                            osmType = cursor.getString(osmTypeIdx) ?: "node",
                            osmId = cursor.getLong(osmIdIdx)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.close()
        }

        return results.sortedBy { it.distanceMeters }.take(limit)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
