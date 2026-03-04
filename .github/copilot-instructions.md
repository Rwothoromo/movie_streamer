# Copilot Instructions for Movie Streamer

## Project Overview

Movie Streamer is an Android TV application built with Kotlin that lets users browse and stream public domain movies. It uses Jetpack Compose for TV for the UI, ExoPlayer (Media3) for video playback, and the TMDB API for movie metadata.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose for TV (`androidx.tv:tv-foundation`, `androidx.tv:tv-material`)
- **Video Playback**: ExoPlayer via Media3 (`androidx.media3`)
- **Architecture**: MVVM with StateFlow
- **Networking**: Retrofit + OkHttp + Gson
- **Image Loading**: Coil (`io.coil-kt:coil-compose`)
- **Coroutines**: `kotlinx-coroutines-android`
- **Min SDK**: 21 (Android 5.0), **Target SDK**: 34
- **JDK**: 17

## Project Structure

```
app/src/main/java/com/moviestreamer/
├── data/               # Data layer
│   ├── ApiClient.kt    # Retrofit singleton
│   ├── Movie.kt        # Movie data model
│   ├── TmdbApi.kt      # TMDB REST interface
│   └── PublicDomainMovies.kt  # Hard-coded public domain content
├── player/
│   └── PlayerActivity.kt  # ExoPlayer video playback screen
└── ui/
    ├── MainActivity.kt
    ├── HomeScreen.kt   # Main Compose screen
    ├── HomeViewModel.kt
    └── MovieCard.kt    # Reusable movie card composable
```

## Code Style & Conventions

- Follow the official [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html).
- Use `val` over `var` wherever possible.
- Prefer Kotlin coroutines and Flow/StateFlow over callbacks or LiveData.
- All UI components are written as `@Composable` functions using Jetpack Compose for TV.
- Use `remember` and `mutableStateOf` for local UI state inside composables.
- ViewModels expose state via `StateFlow` and accept user intents as plain function calls.

## Android TV / 10-Foot UI Guidelines

- Design all UI for **10-foot viewing**: large text (≥24sp for body, ≥48sp for titles), high contrast, and clear focus indicators.
- Every interactive element **must** handle D-pad focus using `onFocusChanged` and visually indicate the focused state (e.g., scale, border, or color change).
- Use `Modifier.focusable()` / `Modifier.clickable()` appropriately and ensure the focus order is logical for D-pad navigation.
- Prefer horizontal `LazyRow` for content rows and vertical `LazyColumn` for page-level scrolling.
- Use `androidx.tv.material3` components (e.g., `TvCard`, `TvButton`) instead of standard Material3 equivalents where available.

## Building & Testing

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Install on connected device/emulator
./gradlew :app:installDebug
```

- TMDB API key is read from `local.properties` (`tmdb.api.key=YOUR_KEY`). The app functions without it by showing only public domain content.
- Do **not** commit secrets or API keys to source control.

## Content & Legal Guidelines

- Only add streaming content from legal sources (public domain films from Archive.org or similarly licensed sources).
- TMDB integration is for **metadata and poster images only**; do not add playback URLs for TMDB-sourced movies.
- When adding new public domain films, add them to `PublicDomainMovies.kt` and include a verifiable Archive.org URL.

## Contribution Guidelines

- Ensure all new UI maintains the 10-foot design principles and D-pad navigation works correctly.
- Write unit tests for ViewModel logic and data layer changes.
- Keep `PublicDomainMovies.kt` as the single source of truth for hard-coded content.
- Follow existing naming conventions: `*Screen.kt` for full screens, `*Card.kt` for card components, `*ViewModel.kt` for ViewModels.
