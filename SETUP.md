# Quick Setup Guide

This guide will help you get the Movie Streamer Android TV app running in minutes.

## Step 1: Prerequisites

Before you begin, ensure you have:
- ✅ Android Studio Hedgehog (2023.1.1) or newer
- ✅ JDK 11 or higher
- ✅ An Android TV emulator or physical device

## Step 2: Get Your TMDB API Key

The app uses The Movie Database (TMDB) API to fetch movie information.

1. Visit [TMDB](https://www.themoviedb.org/signup)
2. Create a free account (takes 2 minutes)
3. Go to your account settings
4. Navigate to **API** section
5. Click **"Create"** to request an API key
6. Choose **"Developer"** option
7. Fill out the simple form (use any website URL for testing)
8. Copy your **API Key (v3 auth)** - it looks like: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`

## Step 3: Configure the App

1. Open the project in Android Studio
2. Navigate to: `app/src/main/java/com/moviestreamer/tv/repository/MovieRepository.kt`
3. Find this line:
   ```kotlin
   private val apiKey = "YOUR_TMDB_API_KEY_HERE"
   ```
4. Replace `YOUR_TMDB_API_KEY_HERE` with your actual API key:
   ```kotlin
   private val apiKey = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
   ```
5. Save the file

## Step 4: Run the App

### Option A: Using Android Studio
1. Click the **"Run"** button (green play icon)
2. Select your Android TV device or emulator
3. Wait for the build to complete
4. The app will launch automatically

### Option B: Using Command Line
```bash
./gradlew installDebug
```

## Step 5: Navigate the App

Use your TV remote or keyboard:
- **Arrow Keys**: Navigate between movies
- **Enter/OK**: Select a movie to see details or play
- **Back**: Return to the previous screen

## Troubleshooting

### Problem: "Error loading content"
**Solution**: 
- Verify your API key is correctly set in `MovieRepository.kt`
- Check that your device has internet connectivity
- Ensure there are no extra spaces or quotes around the API key

### Problem: Build fails with "SDK not found"
**Solution**:
- Open Android Studio's SDK Manager
- Install Android SDK Platform 34 (or the version specified in build.gradle.kts)
- Sync the project again

### Problem: App doesn't appear in TV launcher
**Solution**:
- The app should appear in the TV's app drawer
- If using an emulator, ensure you selected an "Android TV" emulator image
- Check that `android.software.leanback` is properly declared in AndroidManifest.xml

### Problem: Video won't play
**Solution**:
- The default demo video URL should work
- Check your internet connection
- Ensure the video URL in PlayerActivity is a valid HLS stream (.m3u8)

## Next Steps

Once the app is running:
1. Browse popular movies from TMDB
2. Click on a movie to see the video player
3. Use the player controls to play/pause
4. Press Back to return to the home screen

## Need Help?

- Check the main [README.md](README.md) for more details
- Review [TMDB API documentation](https://developers.themoviedb.org/3)
- Consult [Android TV development guide](https://developer.android.com/training/tv)

Enjoy streaming! 🎬
