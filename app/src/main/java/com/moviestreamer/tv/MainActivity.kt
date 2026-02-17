package com.moviestreamer.tv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.moviestreamer.tv.model.Movie
import com.moviestreamer.tv.ui.HomeScreen
import com.moviestreamer.tv.ui.theme.MovieStreamerTheme

/**
 * Main entry point for the TV app.
 * Uses Jetpack Compose for TV to render the UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MovieStreamerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onMovieClick = { movie ->
                            navigateToPlayer(movie)
                        }
                    )
                }
            }
        }
    }
    
    private fun navigateToPlayer(movie: Movie) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("movie_id", movie.id)
            putExtra("movie_title", movie.title)
            // In a real app, you would pass a real video URL
            // For demo, we'll use a sample HLS stream
            putExtra("video_url", "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
        }
        startActivity(intent)
    }
}
