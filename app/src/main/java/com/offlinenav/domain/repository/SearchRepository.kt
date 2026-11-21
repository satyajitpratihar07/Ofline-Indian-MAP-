package com.offlinenav.domain.repository

import com.offlinenav.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(query: String, nearLat: Double, nearLng: Double): List<SearchResult>
    suspend fun searchByCategory(category: String, nearLat: Double, nearLng: Double): List<SearchResult>
    fun getRecentSearches(): Flow<List<SearchResult>>
    suspend fun saveRecentSearch(result: SearchResult)
    suspend fun clearRecentSearches()
}
