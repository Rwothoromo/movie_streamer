# Copilot Instructions for Movie Streamer

## Project Overview

Movie Streamer is an Android TV application built with Kotlin that lets users browse and stream public domain movies. It uses Jetpack Compose for TV for the UI, ExoPlayer (Media3) for video playback, and the TMDB API for movie metadata.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose for TV (`androidx.tv:tv-foundation`, `androidx.tv:tv-material`)
- **Video Playback**: ExoPlayer via Media3 (`androidx.media3`)
- **Architecture**: Clean Architecture with MVVM, StateFlow, and Use Cases
- **Dependency Injection**: Koin (`io.insert-koin:koin-android`)
- **Networking**: Retrofit + OkHttp + Gson
- **Image Loading**: Coil (`io.coil-kt:coil-compose`)
- **Coroutines**: `kotlinx-coroutines-android`
- **Min SDK**: 21 (Android 5.0), **Target SDK**: 34
- **JDK**: 17

## Project Structure

```
app/src/main/java/com/moviestreamer/
├── MovieStreamerApplication.kt  # Application class with Koin initialization
├── data/                        # Data layer
│   ├── AuthInterceptor.kt      # TMDB API authentication interceptor
│   ├── Movie.kt                # Movie data model
│   ├── TmdbApi.kt              # TMDB REST interface
│   ├── PublicDomainMovies.kt   # Hard-coded public domain content
│   └── repository/             # Repository pattern implementations
│       ├── MovieRepository.kt
│       └── MovieRepositoryImpl.kt
├── di/                          # Dependency injection modules
│   ├── NetworkModule.kt        # Retrofit, OkHttp, API setup
│   ├── RepositoryModule.kt     # Repository dependencies
│   ├── UseCaseModule.kt        # Use case dependencies
│   └── ViewModelModule.kt      # ViewModel dependencies
├── domain/                      # Domain layer (business logic)
│   └── usecase/                # Use cases for business operations
│       ├── GetPopularMoviesUseCase.kt
│       ├── GetTopRatedMoviesUseCase.kt
│       ├── GetPublicDomainMoviesUseCase.kt
│       └── SearchMoviesUseCase.kt
├── player/
│   └── PlayerActivity.kt       # ExoPlayer video playback with seek bar & back button
└── ui/                          # UI layer
    ├── MainActivity.kt         # Main entry point
    ├── HomeScreen.kt           # Main Compose screen
    ├── HomeViewModel.kt        # Home screen ViewModel
    └── MovieCard.kt            # Reusable movie card composable
```

## Code Style & Conventions

- Follow the official [Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html).
- Use `val` over `var` wherever possible.
- Prefer Kotlin coroutines and Flow/StateFlow over callbacks or LiveData.
- All UI components are written as `@Composable` functions using Jetpack Compose for TV.
- Use `remember` and `mutableStateOf` for local UI state inside composables.
- ViewModels expose state via `StateFlow` and accept user intents as plain function calls.

## Architecture Patterns

The app follows **Clean Architecture** principles with clear separation of concerns:

- **Presentation Layer** (`ui/`): Jetpack Compose screens and ViewModels
- **Domain Layer** (`domain/`): Business logic in use cases (e.g., `GetPopularMoviesUseCase`)
- **Data Layer** (`data/`): Repositories, API interfaces, and data models
- **Dependency Injection** (`di/`): Koin modules for dependency management

### Dependency Injection with Koin

- Use Koin for dependency injection (not Hilt or Dagger)
- Define dependencies in module files: `NetworkModule.kt`, `RepositoryModule.kt`, `UseCaseModule.kt`, `ViewModelModule.kt`
- Initialize Koin in `MovieStreamerApplication.onCreate()`
- Inject ViewModels using `by viewModel()` in Activities
- Use constructor injection for repositories and use cases

### Use Cases

- Each use case represents a single business operation
- Use cases are injected into ViewModels
- Keep use cases focused and testable
- Use `Result` or sealed classes for error handling

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

## Video Player Features

The `PlayerActivity` provides a TV-optimized video playback experience:

- **ExoPlayer Integration**: Uses Media3 ExoPlayer for robust video playback
- **Seek Bar**: Built-in progress bar showing current position and duration
- **TV Controls**: Controller visible by default (10 second timeout), optimized for D-pad navigation
- **Back Navigation**: 
  - Visible back button in top-left corner
  - D-pad Left key returns to home
  - Physical Back button support
- **Playback Controls**:
  - Play/Pause (Center/Enter key)
  - Fast Forward (Right arrow or FF button)
  - Rewind (Left arrow or Rewind button)
- **Lifecycle Management**: Proper player release on activity stop/destroy
- **Error Handling**: Displays error messages for playback failures

## Content & Legal Guidelines

- Only add streaming content from legal sources (public domain films from Archive.org or similarly licensed sources).
- TMDB integration is for **metadata and poster images only**; do not add playback URLs for TMDB-sourced movies.
- When adding new public domain films, add them to `PublicDomainMovies.kt` and include a verifiable Archive.org URL.

## Contribution Guidelines

- Ensure all new UI maintains the 10-foot design principles and D-pad navigation works correctly.
- Write unit tests for ViewModel logic and data layer changes.
- Follow Clean Architecture principles: separate presentation, domain, and data concerns.
- Add new use cases in `domain/usecase/` for business logic.
- Define dependencies in appropriate Koin modules in `di/`.
- Keep `PublicDomainMovies.kt` as the single source of truth for hard-coded content.
- Follow existing naming conventions: `*Screen.kt` for full screens, `*Card.kt` for card components, `*ViewModel.kt` for ViewModels, `*UseCase.kt` for use cases.
