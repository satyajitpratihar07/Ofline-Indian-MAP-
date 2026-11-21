package com.offlinenav.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.SearchResult
import com.offlinenav.domain.repository.LocationRepository
import com.offlinenav.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<SearchResult> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            searchRepository.getRecentSearches().collect { recents ->
                _uiState.value = _uiState.value.copy(recentSearches = recents)
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery, selectedCategory = null)
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), isLoading = false)
            return
        }

        searchJob = viewModelScope.launch {
            delay(250) // Debounce typing
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userLoc = locationRepository.getLastKnownLocation()
            val lat = userLoc?.latitude ?: 28.6139
            val lng = userLoc?.longitude ?: 77.2090

            val results = searchRepository.search(newQuery, lat, lng)
            _uiState.value = _uiState.value.copy(results = results, isLoading = false)
        }
    }

    fun selectCategory(categoryKey: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = categoryKey, query = "")
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userLoc = locationRepository.getLastKnownLocation()
            val lat = userLoc?.latitude ?: 28.6139
            val lng = userLoc?.longitude ?: 77.2090

            val results = searchRepository.searchByCategory(categoryKey, lat, lng)
            _uiState.value = _uiState.value.copy(results = results, isLoading = false)
        }
    }

    fun selectResult(result: SearchResult) {
        viewModelScope.launch {
            searchRepository.saveRecentSearch(result)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            searchRepository.clearRecentSearches()
        }
    }
}
