package com.offlinenav.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.DownloadRegion
import com.offlinenav.domain.repository.DownloadRepository
import com.offlinenav.download.storage.MapStorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadUiState(
    val availableRegions: List<DownloadRegion> = emptyList(),
    val downloadedRegions: List<DownloadRegion> = emptyList(),
    val searchQuery: String = "",
    val usedStorageBytes: Long = 0L,
    val freeStorageBytes: Long = 0L
)

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val storageManager: MapStorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadUiState())
    val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

    init {
        refreshStorageInfo()

        downloadRepository.getAvailableRegions()
            .onEach { list -> _uiState.value = _uiState.value.copy(availableRegions = list) }
            .launchIn(viewModelScope)

        downloadRepository.getDownloadedRegions()
            .onEach { list ->
                _uiState.value = _uiState.value.copy(downloadedRegions = list)
                refreshStorageInfo()
            }
            .launchIn(viewModelScope)
    }

    fun startDownload(regionId: String) {
        viewModelScope.launch {
            downloadRepository.startDownload(regionId)
        }
    }

    fun pauseDownload(regionId: String) {
        viewModelScope.launch {
            downloadRepository.pauseDownload(regionId)
        }
    }

    fun deleteRegion(regionId: String) {
        viewModelScope.launch {
            downloadRepository.deleteRegion(regionId)
            refreshStorageInfo()
        }
    }

    fun onSearchQueryChanged(q: String) {
        _uiState.value = _uiState.value.copy(searchQuery = q)
    }

    private fun refreshStorageInfo() {
        _uiState.value = _uiState.value.copy(
            usedStorageBytes = storageManager.getUsedMapStorageBytes(),
            freeStorageBytes = storageManager.getAvailableDiskSpaceBytes()
        )
    }
}
