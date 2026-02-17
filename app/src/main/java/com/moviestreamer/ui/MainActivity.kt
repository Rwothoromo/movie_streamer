package com.moviestreamer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.moviestreamer.BuildConfig
import com.moviestreamer.player.PlayerActivity

class MainActivity : ComponentActivity() {
    // HomeViewModel currently has no constructor dependencies, so we use the default factory.
    // If HomeViewModel starts accepting parameters, introduce a ViewModelProvider.Factory or DI.
    private val viewModel: HomeViewModel by viewModels()
    
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
                    // Only play movies with video URLs (public domain content)
                    if (movie.videoUrl != null) {
                        val intent = Intent(this, PlayerActivity::class.java).apply {
                            putExtra("VIDEO_URL", movie.videoUrl)
                            putExtra("MOVIE_TITLE", movie.title)
                        }
                        startActivity(intent)
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
