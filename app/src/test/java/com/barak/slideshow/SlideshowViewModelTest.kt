package com.barak.slideshow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.barak.slideshow.data.MediaItem
import com.barak.slideshow.data.PlaylistRepository
import com.barak.slideshow.domain.SlideshowViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SlideshowViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: SlideshowViewModel
    private val repository: PlaylistRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SlideshowViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test loadPlaylists success`() = runTest {
        val fakeMediaItems = listOf(
            MediaItem(url = "http://example.com/media1.jpg", duration = 10),
            MediaItem(url = "http://example.com/media2.mp4", duration = 5)
        )

        // Mock repository response
        coEvery { repository.fetchPlaylists(any()) } returns fakeMediaItems

        // Trigger loadPlaylists
        viewModel.loadPlaylists("screenKey")

        // Wait for the first non-empty playlist to be emitted
        val playlists = viewModel.playlists.first { it.isNotEmpty() }

        // Verify playlists state is updated
        assertEquals(fakeMediaItems, playlists)
        assertEquals(false, viewModel.loading.value) // Loading should be false after success
        assertEquals(null, viewModel.error.value) // Error should be null on success
    }

    @Test
    fun `test loadPlaylists failure`() = runTest {
        // Mock repository to throw an exception
        coEvery { repository.fetchPlaylists(any()) } throws Exception("Network Error")

        // Trigger loadPlaylists
        viewModel.loadPlaylists("screenKey")

        // Wait for the error to be emitted
        val error = viewModel.error.first { it != null }

        // Verify error state is updated
        assertEquals("Failed to load playlists: Network Error", error)
        assertEquals(false, viewModel.loading.value) // Loading should be false after failure
        assertEquals(emptyList<MediaItem>(), viewModel.playlists.value) // Playlists should remain empty on failure
    }

    @Test
    fun `test togglePlayPause`() {
        // Initial play state should be true (playing)
        assertEquals(true, viewModel.isPlaying.value)

        // Toggle to pause
        viewModel.togglePlayPause()
        assertEquals(false, viewModel.isPlaying.value)

        // Toggle back to play
        viewModel.togglePlayPause()
        assertEquals(true, viewModel.isPlaying.value)
    }
}