package com.barak.slideshow

import com.barak.slideshow.data.MediaItem
import com.barak.slideshow.data.NoviSignApi
import com.barak.slideshow.data.Playlist
import com.barak.slideshow.data.PlaylistItem
import com.barak.slideshow.data.PlaylistRepository
import com.barak.slideshow.data.PlaylistResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PlaylistRepositoryTest {

    private lateinit var repository: PlaylistRepository
    private val api: NoviSignApi = mockk()

    @Before
    fun setUp() {
        repository = PlaylistRepository(api)
    }

    @Test
    fun `fetchPlaylists returns transformed list of MediaItems`() = runTest {
        // Mock API response
        val fakeResponse = PlaylistResponse(
            screenKey = "screenKey",
            playlists = listOf(
                Playlist(
                    playlistItems = listOf(
                        PlaylistItem(creativeKey = "media1.jpg", duration = 5),
                        PlaylistItem(creativeKey = "media2.mp4", duration = 0)
                    )
                )
            )
        )

        coEvery { api.getPlaylists("screenKey") } returns fakeResponse

        // Expected transformed result
        val expectedMediaItems = listOf(
            MediaItem(url = "https://test.onsignage.com/PlayerBackend/creative/get/media1.jpg", duration = 5000L),
            MediaItem(url = "https://test.onsignage.com/PlayerBackend/creative/get/media2.mp4", duration = 3000L)
        )

        // Fetch playlists and verify transformation
        val result = repository.fetchPlaylists("screenKey")
        assertEquals(expectedMediaItems, result)
    }

    @Test
    fun `fetchPlaylists applies default duration when duration is missing or zero`() = runTest {
        // Mock API response with zero duration
        val fakeResponse = PlaylistResponse(
            screenKey = "screenKey",
            playlists = listOf(
                Playlist(
                    playlistItems = listOf(
                        PlaylistItem(creativeKey = "media3.jpg", duration = 0)
                    )
                )
            )
        )

        coEvery { api.getPlaylists("screenKey") } returns fakeResponse

        // Expected transformed result with default duration
        val expectedMediaItems = listOf(
            MediaItem(url = "https://test.onsignage.com/PlayerBackend/creative/get/media3.jpg", duration = 3000L)
        )

        // Fetch playlists and verify transformation with default duration
        val result = repository.fetchPlaylists("screenKey")
        assertEquals(expectedMediaItems, result)
    }
}