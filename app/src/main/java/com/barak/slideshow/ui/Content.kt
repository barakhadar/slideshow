package com.barak.slideshow.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.barak.slideshow.domain.SlideshowViewModel
import com.barak.slideshow.ui.theme.SlideshowTheme

@Composable
fun Content() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        SlideshowScreen(screenKey = "e490b14d-987d-414f-a822-1e7703b37ce4")
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    Content()
}