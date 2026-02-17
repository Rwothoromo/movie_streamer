package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviestreamer.data.Movie

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 32.dp)
                ) {
                    // App Title
                    item {
                        Text(
                            text = "Movie Streamer",
                            modifier = Modifier.padding(start = 48.dp, bottom = 24.dp),
                            color = Color.White,
                            fontSize = 48.sp
                        )
                    }
                    
                    // Public Domain Movies (always available)
                    if (uiState.publicDomainMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = "Public Domain Classics",
                                movies = uiState.publicDomainMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                    
                    // Popular Movies (from TMDB)
                    if (uiState.popularMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = "Popular Movies",
                                movies = uiState.popularMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                    
                    // Top Rated Movies (from TMDB)
                    if (uiState.topRatedMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = "Top Rated Movies",
                                movies = uiState.topRatedMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                }
            }
        }
    }
}
