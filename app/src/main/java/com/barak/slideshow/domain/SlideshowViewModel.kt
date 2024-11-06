package com.barak.slideshow.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barak.slideshow.data.MediaItem
import com.barak.slideshow.data.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SlideshowViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<MediaItem>>(emptyList())
    val playlists: StateFlow<List<MediaItem>> = _playlists.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun loadPlaylists(screenKey: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _loading.value = true
                _error.value = null
                try {
                    val mediaItems = playlistRepository.fetchPlaylists(screenKey)
                    _playlists.value = mediaItems
                } catch (e: Exception) {
                    _error.value = "Failed to load playlists: ${e.message}"
                } finally {
                    _loading.value = false
                }
            }
        }
    }
}