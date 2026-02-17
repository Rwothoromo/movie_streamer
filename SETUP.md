# Developer Setup Guide

## Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

1. **Java Development Kit (JDK) 17**
   ```bash
   # Verify installation
   java -version
   # Should show OpenJDK 17 or Oracle JDK 17
   ```

2. **Android Studio Hedgehog (2023.1.1) or later**
   - Download from: https://developer.android.com/studio
   - Includes Android SDK and Gradle

3. **Android SDK**
   - Minimum SDK: 21 (Android 5.0)
   - Target SDK: 34 (Android 14)
   - Compile SDK: 34

### Recommended Software

1. **Git**
   ```bash
   git --version
   ```

2. **Android TV Emulator** (for testing)
   - In Android Studio: Tools > Device Manager > Create Device
   - Choose TV category
   - Recommended: 1080p or 720p TV profile

## Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Rwothoromo/movie_streamer.git
cd movie_streamer
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click "Open an Existing Project"
3. Navigate to the cloned repository
4. Click "OK"
5. Wait for Gradle sync to complete

### 3. Configure Android SDK

The project should automatically detect your Android SDK. If not:

1. Go to **File > Project Structure > SDK Location**
2. Set **Android SDK location** to your SDK path
   - macOS: `~/Library/Android/sdk`
   - Linux: `~/Android/Sdk`
   - Windows: `C:\Users\<username>\AppData\Local\Android\Sdk`

### 4. Sync Project with Gradle Files

1. Click **File > Sync Project with Gradle Files**
2. Wait for sync to complete
3. Resolve any dependency issues

## Configuration

### TMDB API Key (Optional but Recommended)

To enable movie metadata from The Movie Database:

1. **Get API Key**:
   - Go to https://www.themoviedb.org/
   - Create a free account
   - Navigate to Settings > API
   - Request an API key (select "Developer" option)
   - Copy your API key

2. **Configure in Project**:
   
   **Option A: Using build.gradle.kts (Recommended)**
   
   Edit `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "TMDB_API_KEY", "\"YOUR_ACTUAL_API_KEY_HERE\"")
   ```
   
   **Option B: Using local.properties (More Secure)**
   
   Add to `local.properties` (this file is not committed to git):
   ```properties
   tmdb.api.key=YOUR_ACTUAL_API_KEY_HERE
   ```
   
   Then update `app/build.gradle.kts`:
   ```kotlin
   val tmdbApiKey = project.findProperty("tmdb.api.key")?.toString() ?: "YOUR_TMDB_API_KEY_HERE"
   buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
   ```

3. **Sync Gradle** after making changes

**Note**: The app works without a TMDB API key by showing only public domain content.

## Building the Project

### Debug Build

**From Android Studio**:
1. Select **Build > Make Project** (Ctrl+F9 / Cmd+F9)
2. Wait for build to complete

**From Command Line**:
```bash
# macOS/Linux
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

Build output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
# macOS/Linux
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

For Play Store, you'll need to sign the release build. See **Signing Configuration** below.

## Running the App

### Using Android Studio

1. **Set up Android TV Emulator**:
   - Tools > Device Manager
   - Click "Create Device"
   - Category: TV
   - Choose a TV profile (e.g., "1080p, API 34")
   - Click "Finish"

2. **Run the App**:
   - Select the TV emulator from device dropdown
   - Click Run button (green triangle) or press Shift+F10
   - App will install and launch on emulator

### Using Physical Android TV Device

1. **Enable Developer Options** on your Android TV:
   - Go to Settings > About
   - Click "Build" 7 times
   - Developer options will be enabled

2. **Enable ADB Debugging**:
   - Go to Settings > Developer options
   - Enable "USB debugging"
   - Enable "Network debugging" (optional, for WiFi connection)

3. **Connect Device**:
   
   **USB Connection**:
   ```bash
   # Connect device via USB
   adb devices
   # Should show your device
   ```
   
   **WiFi Connection**:
   ```bash
   # First connect via USB, then:
   adb tcpip 5555
   # Find TV's IP address in Settings > Network
   adb connect <TV_IP_ADDRESS>:5555
   # Now you can disconnect USB
   ```

4. **Install App**:
   ```bash
   ./gradlew installDebug
   # or
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Testing

### D-pad Navigation

Use the emulator's D-pad controls or keyboard:
- **Arrow Keys**: Navigate between items
- **Enter/Return**: Select item
- **Backspace/Esc**: Go back

### Test Checklist

- [ ] App launches to home screen
- [ ] Public domain movies display
- [ ] Can navigate with D-pad between movies
- [ ] Focus border appears on selected movie
- [ ] Clicking a movie launches player
- [ ] Video plays correctly
- [ ] Playback controls work (play/pause, seek)
- [ ] Back button exits player
- [ ] TMDB movies display (if API key configured)
- [ ] Images load correctly
- [ ] App handles no internet gracefully

## Troubleshooting

### Build Errors

**"SDK location not found"**
```bash
# Create local.properties manually
echo "sdk.dir=/path/to/your/android/sdk" > local.properties
```

**"Failed to resolve: androidx.tv:tv-foundation"**
- Ensure you have internet connection
- Check that Google Maven repository is accessible
- Try: File > Invalidate Caches / Restart

**"Unsupported class file major version"**
- Ensure you're using JDK 17
- In Android Studio: File > Project Structure > SDK Location > JDK location

### Runtime Errors

**"Network security config"** 
- This is expected when testing with HTTP URLs
- Archive.org uses HTTPS, so should work fine
- For development, you can add a network security config

**"Failed to load video"**
- Check internet connection
- Verify video URL is accessible
- Check LogCat for detailed errors

**"Focus not working in emulator"**
- Ensure you're using TV emulator, not phone/tablet
- Try using keyboard instead of mouse
- Check that D-pad mode is enabled

### Debugging

**View Logs**:
```bash
# Filter by app package
adb logcat | grep com.moviestreamer

# In Android Studio
# View > Tool Windows > Logcat
```

**Common Log Tags**:
- `MovieStreamer`: General app logs
- `ExoPlayer`: Video playback logs
- `OkHttp`: Network request logs

## IDE Configuration

### Recommended Android Studio Plugins

1. **Kotlin** (built-in)
2. **Compose Multiplatform** (for better Compose support)
3. **ADB Idea** (for quick ADB commands)

### Code Style

The project uses the official Kotlin code style:
1. Settings > Editor > Code Style > Kotlin
2. Set from > Kotlin style guide
3. Apply

### Live Templates

Useful for Compose development:
1. Settings > Editor > Live Templates
2. Enable Compose templates

## Project Structure

```
movie_streamer/
├── app/                        # Main application module
│   ├── build.gradle.kts       # App-level build configuration
│   ├── proguard-rules.pro     # ProGuard rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/moviestreamer/
│           │   ├── data/      # Data layer
│           │   ├── player/    # Video player
│           │   └── ui/        # UI components
│           └── res/           # Resources
├── build.gradle.kts           # Project-level build configuration
├── settings.gradle.kts        # Project settings
├── gradle.properties          # Gradle properties
├── local.properties           # Local SDK paths (not in git)
└── README.md                  # Main documentation
```

## Next Steps

After successful setup:

1. **Explore the Code**:
   - Start with `MainActivity.kt`
   - Review `HomeScreen.kt` for UI structure
   - Check `PlayerActivity.kt` for video playback

2. **Customize**:
   - Add more public domain movies in `PublicDomainMovies.kt`
   - Modify theme colors in `res/values/colors.xml`
   - Update app icon and banner

3. **Extend Features**:
   - Add search functionality
   - Implement favorites
   - Add movie details screen
   - Integrate more content sources

## Additional Resources

- **Android TV Development**: https://developer.android.com/tv
- **Jetpack Compose for TV**: https://developer.android.com/jetpack/androidx/releases/tv
- **ExoPlayer Documentation**: https://exoplayer.dev/
- **TMDB API Docs**: https://developers.themoviedb.org/3

## Getting Help

- **Issues**: https://github.com/Rwothoromo/movie_streamer/issues
- **Android TV Slack**: https://androidtv.slack.com/
- **Stack Overflow**: Tag questions with `android-tv`, `jetpack-compose`, `exoplayer`

---

Happy coding! 🎬📺
