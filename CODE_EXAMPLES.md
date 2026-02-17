# Code Examples and Patterns

This document provides detailed code examples and patterns used in the Movie Streamer app, directly aligned with the tech stack mentioned in the project requirements.

## Table of Contents

1. [Network Layer with Retrofit](#network-layer-with-retrofit)
2. [ExoPlayer Integration](#exoplayer-integration)
3. [TV-Optimized UI Components](#tv-optimized-ui-components)
4. [Focus Management](#focus-management)
5. [10-Foot UI Patterns](#10-foot-ui-patterns)

## Network Layer with Retrofit

### TMDB API Integration

As mentioned in the requirements, the app uses Retrofit for API calls. Here's the implementation:

```kotlin
// app/src/main/java/com/moviestreamer/tv/network/TMDBApiService.kt

interface MovieService {
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String): MovieResponse
    
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String): MovieResponse
    
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("api_key") apiKey: String): MovieResponse
}
```

### Retrofit Configuration

```kotlin
// app/src/main/java/com/moviestreamer/tv/network/RetrofitClient.kt

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val tmdbApiService: TMDBApiService = retrofit.create(TMDBApiService::class.java)
}
```

### Usage in Repository

```kotlin
// app/src/main/java/com/moviestreamer/tv/repository/MovieRepository.kt

class MovieRepository {
    private val apiKey = "YOUR_TMDB_API_KEY_HERE"
    private val apiService = RetrofitClient.tmdbApiService
    
    suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            val response = apiService.getPopularMovies(apiKey)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## ExoPlayer Integration

### ExoPlayer Setup (Media3)

As specified in the requirements, the app uses ExoPlayer (Media3) for video playback:

```kotlin
// app/src/main/java/com/moviestreamer/tv/ui/PlayerScreen.kt

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(videoUrl: String) {
    val context = LocalContext.current
    
    // Create ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }
    
    // Clean up when leaving
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    
    // Display player
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
```

### Supported Formats

ExoPlayer handles:
- **HLS streams** (.m3u8): `media3-exoplayer-hls`
- **DASH streams**: `media3-exoplayer-dash`
- **Adaptive bitrate**: Automatically adjusts quality based on internet speed

### Example Video URLs

```kotlin
// HLS test stream (free)
val hlsUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

// IPTV playlist example
val iptvUrl = "https://iptv-org.github.io/iptv/countries/us.m3u8"

// YouTube trailer (via API)
val youtubeUrl = "https://youtube.com/watch?v=..."
```

## TV-Optimized UI Components

### Movie Card with Focus Animation

The requirements emphasize the need for a "10-foot UI" with clear focus indicators:

```kotlin
// app/src/main/java/com/moviestreamer/tv/ui/MovieCard.kt

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    
    // Animate elevation and scale based on focus
    val cardElevation by animateDpAsState(
        targetValue = if (isFocused) 16.dp else 4.dp
    )
    
    val scale by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 0.dp
    )
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(300.dp)
            .padding(scale)  // Creates "growing" effect
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation
        ),
        border = if (isFocused) {
            BorderStroke(3.dp, Color.White)  // Glowing border
        } else null
    ) {
        // Card content
        AsyncImage(
            model = movie.getPosterUrl(),
            contentDescription = movie.title
        )
    }
}
```

### Horizontal Ribbon Layout

Netflix-style horizontal scrolling rows:

```kotlin
// app/src/main/java/com/moviestreamer/tv/ui/HomeScreen.kt

@Composable
fun MovieRow(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }
    }
}
```

## Focus Management

### D-Pad Navigation

The app is fully navigable with TV remote (D-Pad):

```kotlin
// Focus handling in Compose
Card(
    onClick = onClick,
    modifier = Modifier.onFocusChanged { focusState ->
        when {
            focusState.isFocused -> {
                // Element gained focus
                onFocus()
            }
            else -> {
                // Element lost focus
                onFocusLost()
            }
        }
    }
)
```

### Focus Order

Natural focus flow (left-to-right, top-to-bottom) is automatic with LazyRow/LazyColumn.

## 10-Foot UI Patterns

### Typography for TV

Large text sizes for readability from 10 feet away:

```kotlin
// app/src/main/java/com/moviestreamer/tv/ui/theme/Type.kt

val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 48.sp,  // Large for main titles
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontSize = 24.sp,  // Category titles
        fontWeight = FontWeight.SemiBold
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp   // Minimum readable size
    )
)
```

### High Contrast Dark Theme

```kotlin
// app/src/main/java/com/moviestreamer/tv/ui/theme/Theme.kt

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6200EE),
    background = Color(0xFF121212),  // Almost black
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,      // High contrast
    onSurface = Color.White
)
```

### Spacing and Padding

Generous spacing for TV viewing:

```kotlin
Box(
    modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)  // Large outer padding
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp)  // Space between rows
    ) {
        // Content
    }
}
```

## Data Model Example

### Movie Data Class

```kotlin
// app/src/main/java/com/moviestreamer/tv/model/Movie.kt

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String
) {
    fun getPosterUrl(): String {
        return if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else ""
    }
}
```

## Content Sources Integration

### TMDB API (Metadata)

```kotlin
// Get popular movies
suspend fun getPopularMovies(): Result<List<Movie>> {
    return try {
        val response = apiService.getPopularMovies(apiKey)
        Result.success(response.results)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Video Sources (Examples)

```kotlin
// Legal free content sources
object VideoSources {
    // Public domain content from Archive.org
    const val ARCHIVE_API = "https://archive.org/advancedsearch.php"
    
    // YouTube Data API for trailers
    const val YOUTUBE_API = "https://www.googleapis.com/youtube/v3/"
    
    // Test HLS streams
    const val TEST_STREAM = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    
    // IPTV playlists (free-to-air channels)
    const val IPTV_ORG = "https://github.com/iptv-org/iptv"
}
```

## State Management with Flow

### ViewModel with StateFlow

```kotlin
// app/src/main/java/com/moviestreamer/tv/viewmodel/MovieViewModel.kt

class MovieViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPopularMovies().fold(
                onSuccess = { movies ->
                    _uiState.value = UiState.Success(movies)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Error")
                }
            )
        }
    }
}
```

### Collecting State in Compose

```kotlin
@Composable
fun HomeScreen(viewModel: MovieViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> MovieList(state.movies)
        is UiState.Error -> ErrorMessage(state.message)
    }
}
```

## Dependencies Reference

All dependencies as specified in requirements:

```kotlin
// app/build.gradle.kts

dependencies {
    // Jetpack Compose for TV
    implementation("androidx.tv:tv-foundation:1.0.0-alpha10")
    implementation("androidx.tv:tv-material:1.0.0-alpha10")
    
    // ExoPlayer (Media3)
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.2.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    
    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

## Best Practices

1. **Always release ExoPlayer**: Use `DisposableEffect` to clean up
2. **Handle focus states**: Provide clear visual feedback
3. **Use suspend functions**: For network calls with coroutines
4. **Error handling**: Always wrap API calls in try-catch
5. **Immutable state**: Use data classes with val properties
6. **Separation of concerns**: Follow MVVM architecture

These examples demonstrate the implementation of all key features mentioned in the project requirements, using modern Android TV development practices with Kotlin, Jetpack Compose for TV, and ExoPlayer (Media3).
