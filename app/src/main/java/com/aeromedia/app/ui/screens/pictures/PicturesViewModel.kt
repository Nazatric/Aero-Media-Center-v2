package com.aeromedia.app.ui.screens.pictures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aeromedia.app.data.media.Photo
import com.aeromedia.app.data.media.PictureStoreRepository
import com.aeromedia.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PicturesUiState(
    val isLoading: Boolean = true,
    val photos: List<Photo> = emptyList(),
    val viewerIndex: Int? = null,
)

/** Real on-device photos via PictureStoreRepository — every thumbnail and
 *  every full-screen image is an actual MediaStore image, never a placeholder.
 *  Respects whatever folders are excluded in Settings. */
class PicturesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PictureStoreRepository(application)
    private val settings = SettingsRepository(application)
    private val _uiState = MutableStateFlow(PicturesUiState())
    val uiState: StateFlow<PicturesUiState> = _uiState.asStateFlow()

    private var loaded = false

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val photos = repository.loadAllPhotos(settings.excludedPictureFolders)
            _uiState.value = _uiState.value.copy(isLoading = false, photos = photos)
        }
    }

    fun openViewer(index: Int) {
        _uiState.value = _uiState.value.copy(viewerIndex = index)
    }

    fun closeViewer() {
        _uiState.value = _uiState.value.copy(viewerIndex = null)
    }

    fun nextPhoto() {
        val state = _uiState.value
        val index = state.viewerIndex ?: return
        if (index < state.photos.lastIndex) _uiState.value = state.copy(viewerIndex = index + 1)
    }

    fun previousPhoto() {
        val state = _uiState.value
        val index = state.viewerIndex ?: return
        if (index > 0) _uiState.value = state.copy(viewerIndex = index - 1)
    }
}
