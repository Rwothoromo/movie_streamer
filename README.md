# Movie Streamer - Android TV Streaming App

A modern Android TV streaming application built with Jetpack Compose for TV and ExoPlayer (Media3). This app demonstrates the "10-foot UI" design pattern optimized for TV viewing with D-Pad navigation.

## Features

- **Jetpack Compose for TV**: Modern declarative UI framework for Android TV
- **ExoPlayer (Media3)**: Industry-standard video playback engine with adaptive streaming support (HLS/DASH)
- **TMDB Integration**: Fetches movie metadata, posters, and information from The Movie Database API
- **10-Foot UI Design**: D-Pad navigable interface with clear focus states
- **Focus Animation**: Cards scale and highlight when focused for better UX
- **Horizontal Scrolling Ribbons**: Netflix-style layout with categorized movie rows

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose for TV
- **Video Player**: ExoPlayer (Media3)
- **Networking**: Retrofit with OkHttp
- **Image Loading**: Coil Compose
- **Architecture**: MVVM with Repository Pattern
- **Async**: Kotlin Coroutines and Flow

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK with API level 21 (minimum) to 34 (target)
- TMDB API Key (free from https://www.themoviedb.org/settings/api)

## Setup Instructions

### 1. Get TMDB API Key

1. Create a free account at [The Movie Database](https://www.themoviedb.org/)
2. Go to Settings → API
3. Request an API key (select "Developer" option)
4. Copy your API key (v3 auth)

### 2. Configure the App

1. Clone this repository
2. Open the project in Android Studio
3. Open `app/src/main/java/com/moviestreamer/tv/repository/MovieRepository.kt`
4. Replace `YOUR_TMDB_API_KEY_HERE` with your actual TMDB API key:

```kotlin
private val apiKey = "your_actual_api_key_here"
```

### 3. Build and Run

1. Connect an Android TV device or start an Android TV emulator
2. Click "Run" in Android Studio or execute:

```bash
./gradlew installDebug
```

## Project Structure

```
app/
├── src/main/
│   ├── java/com/moviestreamer/tv/
│   │   ├── model/              # Data models
│   │   │   └── Movie.kt        # Movie data class and API response
│   │   ├── network/            # Networking layer
│   │   │   ├── TMDBApiService.kt    # Retrofit API interface
│   │   │   └── RetrofitClient.kt    # Retrofit configuration
│   │   ├── repository/         # Data repository
│   │   │   └── MovieRepository.kt   # Movie data source
│   │   ├── viewmodel/          # ViewModels
│   │   │   └── MovieViewModel.kt    # UI state management
│   │   ├── ui/                 # Compose UI components
│   │   │   ├── MovieCard.kt    # TV-optimized movie card
│   │   │   ├── HomeScreen.kt   # Main browsing screen
│   │   │   ├── PlayerScreen.kt # Video player screen
│   │   │   └── theme/          # App theme and typography
│   │   ├── MainActivity.kt     # Main entry point
│   │   └── PlayerActivity.kt   # Video player activity
│   ├── res/                    # Android resources
│   └── AndroidManifest.xml     # App configuration
└── build.gradle.kts            # Build configuration
```

## Key Components

### Movie Card Component

The `MovieCard.kt` demonstrates a TV-optimized card with:
- Focus state animation (scales up when focused)
- Clear border highlighting
- Smooth transitions
- Poster image loading with Coil

### ExoPlayer Integration

The `PlayerScreen.kt` shows how to:
- Create and configure ExoPlayer instance
- Handle HLS/DASH adaptive streaming
- Display player controls
- Manage player lifecycle (prepare, play, release)

### Network Layer

The app uses Retrofit for API calls:
- `TMDBApiService.kt`: Defines API endpoints
- `RetrofitClient.kt`: Configures Retrofit with logging
- `MovieRepository.kt`: Handles data fetching and error handling

## Content Sources

### Metadata APIs (Integrated)

- **TMDB**: Movie information, posters, ratings, cast info

### Video Sources (Examples for Testing)

For testing purposes, you can use these legal free video sources:

1. **HLS Test Streams**:
   - `https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8`

2. **Public Domain Content**:
   - Internet Archive API: `https://archive.org/`
   - YouTube Data API for trailers

3. **IPTV Playlists**:
   - GitHub IPTV-org project for free-to-air channels

**Note**: Currently, the app uses a demo HLS stream. To integrate real movie streams, you need legal content sources.

## Important Compliance Note

⚠️ **Legal Notice**: If you plan to publish this app on Google Play Store:

- Ensure you have rights to all streamed content
- Do not use scrapers for copyrighted material
- Stick to public domain or Creative Commons licensed content
- Apps facilitating piracy will be banned from the Play Store

## D-Pad Navigation

The app is fully navigable with a TV remote:

- **Up/Down/Left/Right**: Navigate between items
- **Center/OK**: Select item
- **Back**: Return to previous screen

## Building for Production

### Create a signed APK

1. Generate a keystore
2. Configure signing in `app/build.gradle.kts`
3. Run:

```bash
./gradlew assembleRelease
```

### Optimize for TV

The app already includes:
- `android.software.leanback` feature requirement
- Landscape orientation lock
- TV launcher intent filter
- Touch screen set as not required

## Troubleshooting

### API Key Issues

If you see an error about loading content:
1. Verify your API key is correctly set in `MovieRepository.kt`
2. Check your internet connection
3. Verify TMDB API service is accessible

### Video Playback Issues

- Ensure the video URL is a valid HLS (.m3u8) or DASH stream
- Check internet connectivity
- Some streams may require CORS headers or specific configuration

## Future Enhancements

Potential improvements for this app:

- Add more content categories (Top Rated, Upcoming, Search)
- Implement proper video URL fetching from legal APIs
- Add user authentication and profiles
- Integrate with more content providers
- Add parental controls
- Implement watchlist functionality
- Add subtitle support
- Integrate with YouTube API for trailers

## License

This project is for educational purposes. Ensure you comply with all content licensing and distribution laws in your jurisdiction.

## Resources

- [Jetpack Compose for TV](https://developer.android.com/jetpack/compose/tv)
- [ExoPlayer Documentation](https://developer.android.com/guide/topics/media/exoplayer)
- [TMDB API Documentation](https://developers.themoviedb.org/3)
- [Android TV Guidelines](https://developer.android.com/training/tv)

## Contributing

Contributions are welcome! Please ensure your code follows the existing style and includes appropriate documentation.
