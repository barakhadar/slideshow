# NoviSign Slideshow App

A simple Android app that fetches and displays media files (images and videos) from the NoviSign API in a looping slideshow. The app includes cross-fade transitions and a display duration for each media item. Built with Kotlin, MVVM architecture, Jetpack Compose, and other modern Android libraries.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Libraries Used](#libraries-used)
- [Setup and Installation](#prerequisites)

## Features

- Fetches playlists from the NoviSign API and displays each item (image or video) in a looping slideshow.
- Cross-fade animation between media items.
- Play/pause control for the slideshow.
- Customizable display duration for each media item.
- Progress bar indicating the time remaining for each item.
- Graceful error handling for network requests.

## Architecture

This project follows the **MVVM (Model-View-ViewModel)** architecture, ensuring a clear separation of concerns and making it easier to test and maintain.

- **Model**: Handles data and business logic. In this app, `PlaylistRepository` fetches data from the `NoviSignApi`.
- **ViewModel**: `SlideshowViewModel` provides the data to the UI and manages its state. It also controls the play/pause functionality.
- **View**: Composables in `SlideshowScreen` handle the display and user interaction.

## Libraries Used

- **[Kotlin](https://kotlinlang.org/)** - Language for all the app’s code.
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - UI toolkit for building declarative UIs.
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection library.
- **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming.
- **[StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)** - State management and data flow.
- **[Retrofit](https://square.github.io/retrofit/)** - HTTP client for making API requests.
- **[ExoPlayer](https://exoplayer.dev/)** - For video playback.
- **[MockK](https://mockk.io/)** - For mocking dependencies in tests.
- **[JUnit](https://junit.org/junit4/)** - Unit testing framework.

## Prerequisites

- Android Studio 4.2 or above
- Minimum Android SDK level 21
- Internet connection (for API requests)
