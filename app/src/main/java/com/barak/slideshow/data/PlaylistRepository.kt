package com.barak.slideshow.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val api: NoviSignApi
) {
    suspend fun fetchPlaylists(screenKey: String): List<MediaItem> {
        val response = api.getPlaylists(screenKey)
        return response.playlists
            .flatMap { it.playlistItems }
            .mapNotNull { item ->
                val url = "https://test.onsignage.com/PlayerBackend/creative/get/${item.creativeKey}"
                val duration = (item.duration * 1000L).takeIf { it > 0 } ?: 3000L // default to 3 seconds if duration is not provided
                MediaItem(url, duration)
            }
    }
}