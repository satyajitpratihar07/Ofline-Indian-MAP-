package com.offlinenav.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinenav.domain.model.PlaceCategory
import com.offlinenav.domain.model.SavedPlace
import com.offlinenav.domain.repository.SavedPlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPlacesViewModel @Inject constructor(
    private val savedPlaceRepository: SavedPlaceRepository
) : ViewModel() {

    private val _savedPlaces = MutableStateFlow<List<SavedPlace>>(emptyList())
    val savedPlaces: StateFlow<List<SavedPlace>> = _savedPlaces.asStateFlow()

    init {
        savedPlaceRepository.getAllSavedPlaces()
            .onEach { list -> _savedPlaces.value = list }
            .launchIn(viewModelScope)
    }

    fun addPlace(name: String, address: String, category: PlaceCategory, lat: Double, lng: Double) {
        viewModelScope.launch {
            savedPlaceRepository.savePlace(
                SavedPlace(
                    name = name,
                    address = address,
                    category = category,
                    latitude = lat,
                    longitude = lng
                )
            )
        }
    }

    fun deletePlace(id: Long) {
        viewModelScope.launch {
            savedPlaceRepository.deletePlace(id)
        }
    }
}
