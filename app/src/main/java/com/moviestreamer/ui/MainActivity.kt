package com.moviestreamer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.moviestreamer.BuildConfig
import com.moviestreamer.player.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Warning about alpha library usage
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "This app uses Compose for TV libraries in alpha stage (1.0.0-alpha10). " +
                    "Alpha libraries may have breaking API changes or stability issues. " +
                    "Monitor https://developer.android.com/jetpack/androidx/releases/tv for stable releases.")
        }
        
        setContent {
            HomeScreen(
                viewModel = viewModel,
                onMovieClick = { movie ->
                    // Only play movies with video URLs (public domain content from Archive.org)
                    // TMDB movies are for browsing metadata only—they have no playable URLs
                    if (movie.videoUrl != null) {
                        val intent = Intent(this, PlayerActivity::class.java).apply {
                            putExtra("VIDEO_URL", movie.videoUrl)
                            putExtra("MOVIE_TITLE", movie.title)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "This movie is for browsing only (no playback available)", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "Movie '${movie.title}' has no playback URL (TMDB movies are metadata-only)")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    
    companion object {
        private const val TAG = "MainActivity"
    }
}
