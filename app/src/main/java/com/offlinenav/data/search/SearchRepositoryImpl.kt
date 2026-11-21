package com.offlinenav.data.search

import android.content.Context
import com.offlinenav.core.data.local.dao.SavedPlaceDao
import com.offlinenav.core.data.local.entity.SavedPlaceEntity
import com.offlinenav.domain.model.SearchResult
import com.offlinenav.domain.repository.SearchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val offlineSearchDb: OfflineSearchDatabase,
    private val savedPlaceDao: SavedPlaceDao
) : SearchRepository {

    private val _recentSearches = MutableStateFlow<List<SearchResult>>(emptyList())

    override fun getRecentSearches(): Flow<List<SearchResult>> = _recentSearches.asStateFlow()

    override suspend fun saveRecentSearch(result: SearchResult) {
        val current = _recentSearches.value.toMutableList()
        current.removeAll { it.id == result.id || (it.name == result.name && it.latitude == result.latitude) }
        current.add(0, result)
        _recentSearches.value = current.take(15)
    }

    override suspend fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    override suspend fun search(query: String, nearLat: Double, nearLng: Double): List<SearchResult> =
        withContext(Dispatchers.IO) {
            val mapsDir = File(context.getExternalFilesDir(null), "maps")
            val results = mutableListOf<SearchResult>()

            if (mapsDir.exists()) {
                mapsDir.listFiles()?.forEach { regionFolder ->
                    val dbFile = File(regionFolder, "search.db")
                    if (dbFile.exists()) {
                        results.addAll(offlineSearchDb.search(dbFile, query, nearLat, nearLng))
                    }
                }
            }

            // Built-in offline fallback locations (e.g. Major Transit Hubs, Hospitals, Landmarks)
            if (results.isEmpty()) {
                results.addAll(getBuiltInFallbackResults(query, nearLat, nearLng))
            }

            results.sortedBy { it.distanceMeters }.distinctBy { it.name + it.address }
        }

    override suspend fun searchByCategory(category: String, nearLat: Double, nearLng: Double): List<SearchResult> =
        withContext(Dispatchers.IO) {
            val mapsDir = File(context.getExternalFilesDir(null), "maps")
            val results = mutableListOf<SearchResult>()

            if (mapsDir.exists()) {
                mapsDir.listFiles()?.forEach { regionFolder ->
                    val dbFile = File(regionFolder, "search.db")
                    if (dbFile.exists()) {
                        results.addAll(offlineSearchDb.searchByCategory(dbFile, category, nearLat, nearLng))
                    }
                }
            }

            if (results.isEmpty()) {
                results.addAll(getBuiltInFallbackCategory(category, nearLat, nearLng))
            }

            results.sortedBy { it.distanceMeters }
        }

    private fun getBuiltInFallbackResults(query: String, nearLat: Double, nearLng: Double): List<SearchResult> {
        val q = query.lowercase().trim()
        val samples = listOf(
            // Major Indian Cities & Hubs
            SearchResult(1, "New Delhi Railway Station", "Bhavbhuti Marg, Ratan Lal Market, New Delhi", "railway_station", 28.6429, 77.2195),
            SearchResult(2, "Indira Gandhi International Airport (DEL)", "Palam, New Delhi", "airport", 28.5562, 77.1000),
            SearchResult(3, "Howrah Railway Station", "Howrah Railway Station, Howrah, West Bengal", "railway_station", 22.5838, 88.3426),
            SearchResult(4, "Netaji Subhash Chandra Bose Airport (CCU)", "Dum Dum, Kolkata, West Bengal", "airport", 22.6547, 88.4467),
            SearchResult(5, "Chhatrapati Shivaji Maharaj Terminus", "Fort, Mumbai, Maharashtra", "railway_station", 18.9402, 72.8356),
            SearchResult(6, "Krantivira Sangolli Rayanna Bengaluru Station", "Majestic, Bengaluru, Karnataka", "railway_station", 12.9784, 77.5684),
            SearchResult(7, "Chennai Central Railway Station", "Kannappar Thidal, Chennai, Tamil Nadu", "railway_station", 13.0827, 80.2755),
            SearchResult(8, "Kolkata City Center", "Park Street, Kolkata, West Bengal", "place", 22.5516, 88.3519),
            SearchResult(9, "Connaught Place", "Rajiv Chowk, Central Delhi, Delhi", "place", 28.6315, 77.2167),
            SearchResult(10, "Bandra Kurla Complex (BKC)", "Bandra East, Mumbai, Maharashtra", "place", 19.0657, 72.8687),
            
            // Major Indian Healthcare & Hospitals
            SearchResult(11, "AIIMS New Delhi", "Sri Aurobindo Marg, Ansari Nagar, New Delhi", "hospital", 28.5672, 77.2100),
            SearchResult(12, "Apollo Gleneagles Hospital", "58 Canal Circular Road, Kolkata, West Bengal", "hospital", 22.5786, 88.4022),
            SearchResult(13, "Fortis Memorial Research Institute", "Sector 44, Gurugram, Haryana", "hospital", 28.4554, 77.0722),
            SearchResult(14, "Lilavati Hospital & Research Centre", "A-791, Bandra West, Mumbai, Maharashtra", "hospital", 19.0514, 72.8291),
            SearchResult(15, "Manipal Hospital", "98 HAL Old Airport Rd, Bengaluru, Karnataka", "hospital", 12.9587, 77.6487),

            // Fuel Stations (Indian Oil, Bharat Petroleum, HP)
            SearchResult(16, "Indian Oil Petrol Pump", "NH-19, Durgapur Expressway, West Bengal", "fuel", 22.7500, 88.2500),
            SearchResult(17, "Bharat Petroleum (BPCL) Fuel Station", "Ring Road, Lajpat Nagar, New Delhi", "fuel", 28.5700, 77.2400),
            SearchResult(18, "Hindustan Petroleum (HP) Auto Care", "Western Express Highway, Andheri, Mumbai", "fuel", 19.1136, 72.8561),
            SearchResult(19, "Shell Petrol Bunk", "Outer Ring Road, Bellandur, Bengaluru", "fuel", 12.9260, 77.6762),

            // Banks & ATMs
            SearchResult(20, "State Bank of India (SBI) Main Branch", "Parliament Street, New Delhi", "atm", 28.6250, 77.2150),
            SearchResult(21, "HDFC Bank & 24/7 ATM", "Salt Lake Sector V, Kolkata, West Bengal", "atm", 22.5800, 88.4300),
            SearchResult(22, "ICICI Bank ATM", "Nariman Point, Mumbai, Maharashtra", "atm", 18.9256, 72.8242),

            // Dining & Restaurants
            SearchResult(23, "Haldiram's Food City", "Chowringhee Road, Kolkata, West Bengal", "restaurant", 22.5450, 88.3500),
            SearchResult(24, "Saravana Bhavan", "Janpath, Connaught Place, New Delhi", "restaurant", 28.6280, 77.2190),
            SearchResult(25, "Bukhara Restaurant", "ITC Maurya, Diplomatic Enclave, New Delhi", "restaurant", 28.5975, 77.1735),
            SearchResult(26, "Karim's Historic Mughlai", "Gali Kababian, Jama Masjid, Old Delhi", "restaurant", 28.6508, 77.2334),
            SearchResult(27, "Bademiya Kebabs", "Tulloch Road, Apollo Bandar, Colaba, Mumbai", "restaurant", 18.9220, 72.8330)
        )
        return samples.filter { it.name.lowercase().contains(q) || it.address.lowercase().contains(q) || it.category.lowercase().contains(q) }
            .map { it.copy(distanceMeters = calculateDist(nearLat, nearLng, it.latitude, it.longitude)) }
    }

    private fun getBuiltInFallbackCategory(category: String, nearLat: Double, nearLng: Double): List<SearchResult> {
        return getBuiltInFallbackResults("", nearLat, nearLng).filter { it.category.equals(category, ignoreCase = true) }
    }

    private fun calculateDist(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }
}
