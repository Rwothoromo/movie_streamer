# Architecture Documentation

This document explains the architecture and design decisions of the Movie Streamer Android TV app.

## Architecture Pattern: MVVM

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern, which provides clear separation of concerns and makes the code more testable and maintainable.

```
┌─────────────┐
│    View     │ (Jetpack Compose UI)
│  (UI Layer) │
└──────┬──────┘
       │ observes StateFlow
       ▼
┌─────────────┐
│  ViewModel  │ (MovieViewModel)
│ (UI Logic)  │
└──────┬──────┘
       │ calls suspend functions
       ▼
┌─────────────┐
│ Repository  │ (MovieRepository)
│(Data Layer) │
└──────┬──────┘
       │ makes API calls
       ▼
┌─────────────┐
│   Network   │ (Retrofit + TMDB API)
│  (Remote)   │
└─────────────┘
```

## Layer Breakdown

### 1. Model Layer (`model/`)

**Purpose**: Define data structures and business entities.

- `Movie.kt`: Data class representing a movie with TMDB fields
- `MovieResponse.kt`: API response wrapper

**Design Decisions**:
- Uses `@SerializedName` for flexible JSON parsing
- Provides helper methods (`getPosterUrl()`) for computed properties
- Immutable data classes for thread safety

### 2. Network Layer (`network/`)

**Purpose**: Handle all network communication with external APIs.

**Components**:
- `TMDBApiService.kt`: Retrofit interface defining API endpoints
- `RetrofitClient.kt`: Singleton providing configured Retrofit instance

**Design Decisions**:
- Uses Retrofit for type-safe HTTP clients
- OkHttp for logging and request intercepting
- Suspend functions for coroutine support
- Centralized base URL configuration

**Example API Call**:
```kotlin
@GET("movie/popular")
suspend fun getPopularMovies(
    @Query("api_key") apiKey: String,
    @Query("page") page: Int = 1
): MovieResponse
```

### 3. Repository Layer (`repository/`)

**Purpose**: Abstract data sources and provide clean API for ViewModels.

**Components**:
- `MovieRepository.kt`: Manages movie data fetching

**Design Decisions**:
- Returns `Result<T>` for explicit success/failure handling
- Encapsulates API key management
- Could be extended to include local caching (Room database)
- Single source of truth pattern

**Error Handling**:
```kotlin
suspend fun getPopularMovies(): Result<List<Movie>> {
    return try {
        val response = apiService.getPopularMovies(apiKey)
        Result.success(response.results)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### 4. ViewModel Layer (`viewmodel/`)

**Purpose**: Manage UI state and handle business logic.

**Components**:
- `MovieViewModel.kt`: Manages movie list state
- `UiState`: Sealed class representing different UI states

**Design Decisions**:
- Uses `StateFlow` for reactive UI updates
- Survives configuration changes
- Handles loading, success, and error states
- `viewModelScope` for automatic coroutine cancellation

**State Management**:
```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val movies: List<Movie>) : UiState()
    data class Error(val message: String) : UiState()
}
```

### 5. UI Layer (`ui/`)

**Purpose**: Render user interface using Jetpack Compose.

**Components**:
- `MovieCard.kt`: Individual movie card with focus animations
- `HomeScreen.kt`: Main browsing screen with horizontal ribbons
- `PlayerScreen.kt`: Video playback screen with ExoPlayer
- `theme/`: Material Design 3 theming

**Design Decisions**:
- Jetpack Compose for declarative UI
- Composable functions are small and focused
- Focus state management for TV navigation
- Animation for better UX

**Focus Management Example**:
```kotlin
var isFocused by remember { mutableStateOf(false) }

Card(
    modifier = Modifier.onFocusChanged { focusState ->
        isFocused = focusState.isFocused
    },
    border = if (isFocused) BorderStroke(3.dp, Color.White) else null
)
```

## Key Design Patterns

### 1. Dependency Injection (Manual)

Currently uses simple object singletons. Could be upgraded to Hilt/Dagger for better testability.

### 2. Observer Pattern

ViewModels expose `StateFlow` which UI components collect and react to.

### 3. Repository Pattern

Abstracts data sources from ViewModels.

### 4. Single Responsibility Principle

Each class has one clear purpose:
- ViewModels manage UI state
- Repositories handle data
- API services define endpoints

## ExoPlayer Integration

**Architecture**:
```
PlayerScreen (Compose)
    └─> ExoPlayer instance
        ├─> MediaItem (HLS/DASH URL)
        └─> PlayerView (Android View in Compose)
```

**Lifecycle Management**:
```kotlin
val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(videoUrl))
        prepare()
    }
}

DisposableEffect(Unit) {
    onDispose { exoPlayer.release() }
}
```

## TV-Specific Design

### Focus System

Android TV uses D-Pad navigation, requiring explicit focus management:

1. **Focus States**: `onFocusChanged` modifier tracks focus
2. **Visual Feedback**: Borders, scaling, elevation changes
3. **Focus Order**: Natural left-to-right, top-to-bottom flow

### 10-Foot UI

Designed for viewing from 10 feet away:
- Large text (48sp for headlines)
- High contrast (dark background, white text)
- Clear focus indicators (3dp white border)
- Spacious layouts (32dp padding)

## Data Flow Example

User selects a movie:
```
1. User clicks movie card
2. MovieCard onClick callback fires
3. HomeScreen's onMovieClick lambda executes
4. MainActivity.navigateToPlayer() called
5. Intent created with movie data
6. PlayerActivity started
7. PlayerScreen composable rendered
8. ExoPlayer initialized with video URL
9. Video begins playing
```

## Future Architectural Improvements

1. **Add Dependency Injection**: Migrate to Hilt for better testability
2. **Local Caching**: Add Room database for offline support
3. **Paging**: Implement Paging 3 for infinite scroll
4. **Use Cases**: Add UseCase layer between ViewModel and Repository
5. **Error Handling**: Create sealed class for different error types
6. **Testing**: Add unit tests for ViewModels and integration tests for Repository

## Threading

- **Main Thread**: UI rendering (Compose)
- **IO Thread**: Network calls (Retrofit with coroutines)
- **Lifecycle-aware**: ViewModelScope automatically cancelled

## State Management

The app uses **unidirectional data flow**:
```
User Action → ViewModel → Repository → API
                ↓
            StateFlow
                ↓
              UI Update
```

This ensures predictable state updates and easier debugging.

## Performance Considerations

1. **Image Loading**: Coil handles caching and memory management
2. **List Performance**: LazyRow for efficient horizontal scrolling
3. **ExoPlayer**: Adaptive streaming reduces buffering
4. **Coroutines**: Non-blocking async operations

## Security Considerations

1. **API Key**: Should be moved to BuildConfig or secrets.properties
2. **HTTPS**: All network calls use secure connections
3. **Content Validation**: URLs should be validated before playback
4. **ProGuard**: Obfuscates code in release builds

## Accessibility

While the app is optimized for TV:
- Focus navigation works with D-Pad
- Content descriptions could be added for screen readers
- High contrast for visibility

This architecture provides a solid foundation for a production Android TV app while remaining simple enough for learning and experimentation.
