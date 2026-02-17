package com.moviestreamer.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.moviestreamer.tv.ui.PlayerScreen
import com.moviestreamer.tv.ui.theme.MovieStreamerTheme

/**
 * Activity for playing videos using ExoPlayer.
 */
class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val videoUrl = intent.getStringExtra("video_url") ?: ""
        val movieTitle = intent.getStringExtra("movie_title") ?: ""
        
        setContent {
            MovieStreamerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreen(
                        videoUrl = videoUrl,
                        title = movieTitle,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}
