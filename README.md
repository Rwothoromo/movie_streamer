# Movie Streamer - Android TV App

A modern Android TV streaming application built with Kotlin, Jetpack Compose for TV, and ExoPlayer.

## Features

- **10-Foot UI Design**: Optimized for TV viewing with D-pad/remote navigation
- **Jetpack Compose for TV**: Modern declarative UI with clear focus states
- **ExoPlayer Integration**: High-quality video playback
- **TMDB API**: Movie metadata for popular and top-rated films
- **Public Domain Content**: Legal streaming from Archive.org
- **D-pad Navigation**: Full remote control support with focus indicators

## Tech Stack

- **Kotlin**: Modern Android development
- **Jetpack Compose for TV**: Declarative UI framework for Android TV
- **ExoPlayer (Media3)**: Video playback engine
- **Retrofit**: HTTP client for API calls
- **Coil**: Image loading library
- **TMDB API**: Movie metadata and information
- **Archive.org**: Public domain video sources

## Prerequisites

- Android Studio Hedgehog or later
- Android SDK 34
- JDK 17
- TMDB API Key (optional but recommended)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/Rwothoromo/movie_streamer.git
cd movie_streamer
```

### 2. Configure TMDB API Key (Optional)

To display movie metadata from TMDB:

1. Get a free API key from [TMDB](https://www.themoviedb.org/settings/api)
2. Open `app/build.gradle.kts`
3. Replace `YOUR_TMDB_API_KEY_HERE` with your actual API key:

```kotlin
buildConfigField("String", "TMDB_API_KEY", "\"your_actual_api_key\"")
```

**Note**: The app will work without a TMDB API key by showing only public domain content.

### 3. Build the Project

```bash
./gradlew assembleDebug
```

### 4. Install and Run

**One-command build, install, and launch:**

```bash
./gradlew :app:installDebug && adb shell am start -n com.moviestreamer/.ui.MainActivity
```

**Or separately:**

```bash
# Install the app
./gradlew :app:installDebug

# Launch the app
adb shell am start -n com.moviestreamer/.ui.MainActivity
```

Alternatively, use Android Studio to build and deploy directly.

## Project Structure

```
app/
├── src/main/
│   ├── java/com/moviestreamer/
│   │   ├── data/              # Data models and API clients
│   │   │   ├── Movie.kt       # Movie data model
│   │   │   ├── TmdbApi.kt     # TMDB API interface
│   │   │   ├── ApiClient.kt   # Retrofit client
│   │   │   └── PublicDomainMovies.kt  # Public domain content
│   │   ├── ui/                # UI components
│   │   │   ├── MainActivity.kt
│   │   │   ├── HomeScreen.kt
│   │   │   ├── MovieCard.kt
│   │   │   └── HomeViewModel.kt
│   │   └── player/            # Video player
│   │       └── PlayerActivity.kt
│   ├── res/                   # Resources
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Usage

### Navigation

- **D-pad/Arrow Keys**: Navigate between movies
- **Enter/Center**: Select a movie to play
- **Back**: Return to the previous screen or exit player

### Available Content

1. **Public Domain Classics**: Classic films from Archive.org (always available, playable)
2. **Popular Movies**: Current popular movies metadata from TMDB (browse only, no playback)
3. **Top Rated Movies**: Highly rated films metadata from TMDB (browse only, no playback)

**Note**: Only public domain movies have playback URLs. TMDB integration provides metadata and posters for browsing, but actual streaming requires legal video sources.

### Video Playback Controls

- **Play/Pause**: Center button or Play/Pause
- **Fast Forward**: Fast Forward button
- **Rewind**: Rewind button
- **Back**: Exit player and return to browse

## Content Sources

### Archive.org (Public Domain)

The app includes curated public domain films from Archive.org:
- Night of the Living Dead (1968)
- Metropolis (1927)
- Nosferatu (1922)
- And more classic films

These films are in the public domain and legally available for streaming.

### TMDB (Metadata Only)

The Movie Database (TMDB) provides metadata including:
- Movie titles and descriptions
- Poster images
- Release dates
- Ratings

**Note**: TMDB movies are for browsing only. Actual video playback requires legal sources.

## Legal Notice

This app only streams content from legal sources:

1. **Public Domain Films**: Content from Archive.org that is in the public domain
2. **No Piracy**: The app does not include or support pirated content scrapers
3. **Metadata Only**: TMDB integration is for movie information only, not streaming

For Play Store deployment, ensure all content sources have appropriate licensing and rights.

## Development

### Building the App

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### Testing on Android TV

1. Enable Developer Options on your Android TV
2. Enable USB Debugging
3. Connect via USB or WiFi (adb connect)
4. Install using `./gradlew installDebug`

### Customization

#### Adding More Public Domain Content

Edit `app/src/main/java/com/moviestreamer/data/PublicDomainMovies.kt` to add more films from Archive.org.

#### Styling

Modify colors and themes in `app/src/main/res/values/`:
- `colors.xml`: Color palette
- `themes.xml`: App theme
- `strings.xml`: Text resources

## Contributing

Contributions are welcome! Please ensure:
1. All content sources are legal and properly licensed
2. Code follows Kotlin style guidelines
3. UI maintains 10-foot design principles
4. D-pad navigation works correctly

## License

This project is open source. See LICENSE file for details.

## Acknowledgments

- [TMDB](https://www.themoviedb.org/) for movie metadata
- [Archive.org](https://archive.org/) for public domain content
- [ExoPlayer](https://exoplayer.dev/) for video playback
- Android Jetpack team for Compose for TV
