package com.barak.slideshow.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.barak.slideshow.domain.SlideshowViewModel
import kotlinx.coroutines.delay

@Composable
fun SlideshowScreen(
    screenKey: String,
    viewModel: SlideshowViewModel = hiltViewModel()
) {
    LaunchedEffect(screenKey) {
        viewModel.loadPlaylists(screenKey)
    }

    val playlists by viewModel.playlists.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    if (error != null) {
        Text(text = error ?: "Unknown error", modifier = Modifier.padding(16.dp))
        return
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (playlists.isEmpty()) {
        Text(text = "No media available", modifier = Modifier.padding(16.dp))
        return
    }

    var currentIndex by remember { mutableStateOf(0) }
    var currentDuration by remember { mutableStateOf(playlists[currentIndex].duration) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf(-1L) }

    // Calculate the progress based on elapsedTime
    val progress by remember {
        derivedStateOf { (elapsedTime / currentDuration.toFloat()).coerceIn(0f, 1f) }
    }

    // Coroutine to manage progress and timing of each slide
    LaunchedEffect(currentIndex, isPlaying) {
        if (isPlaying) {
            // Start tracking time for this slide, taking into account pause/resume
            if (startTime == -1L) {
                startTime = System.currentTimeMillis() - elapsedTime
            }

            // Loop to update elapsed time and progress bar in real-time
            while (elapsedTime < currentDuration && isPlaying) {
                elapsedTime = System.currentTimeMillis() - startTime
                delay(16L) // Update approximately every 16ms for smooth progress
            }

            // Move to the next slide if this slide has finished
            if (elapsedTime >= currentDuration) {
                startTime = System.currentTimeMillis()
                elapsedTime = 0L
                currentIndex = (currentIndex + 1) % playlists.size
                currentDuration = playlists[currentIndex].duration
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        playlists.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = index == currentIndex,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                MediaDisplay(item.url)
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            color = Color.Red,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 35.dp)
        )

        IconButton(
            onClick = {
                if (isPlaying) {
                    // Pausing
                    viewModel.togglePlayPause()
                } else {
                    // Resuming from where we left off
                    startTime = System.currentTimeMillis() - elapsedTime // Adjust start time to continue
                    viewModel.togglePlayPause()
                }
            },
            modifier = Modifier
                .padding(top = 52.dp, end = 16.dp)
                .width(50.dp)
                .height(50.dp)
                .align(Alignment.TopEnd)
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(25)
                )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
    }
}

@Composable
fun MediaDisplay(mediaUrl: String) {
    val isImage = mediaUrl.endsWith(".jpg") || mediaUrl.endsWith(".png")

    if (isImage) {
        Image(
            painter = rememberAsyncImagePainter(
                model = mediaUrl,
                imageLoader = ImageLoader.Builder(LocalContext.current)
                    .diskCachePolicy(CachePolicy.ENABLED) // Enable caching
                    .build()
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        VideoPlayer(videoUrl = mediaUrl)
    }
}

@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}