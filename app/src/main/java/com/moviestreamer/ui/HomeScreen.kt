package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviestreamer.R
import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingEntity

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Movie) -> Unit,
    onTvShowClick: (TvShow) -> Unit,
    onSearchClick: () -> Unit,
    onGenreClick: () -> Unit,
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
                    text = stringResource(R.string.error_loading),
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
                            text = stringResource(R.string.app_name),
                            modifier = Modifier.padding(start = 48.dp, bottom = 16.dp),
                            color = Color.White,
                            fontSize = 48.sp
                        )
                    }

                    // Navigation buttons
                    item {
                        Row(modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)) {
                            TextButton(onClick = onSearchClick) {
                                Text("🔍 Search", color = Color.White, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            TextButton(onClick = onGenreClick) {
                                Text("🎭 Browse Genres", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }

                    // Continue Watching
                    if (uiState.continueWatching.isNotEmpty()) {
                        item {
                            ContinueWatchingRow(
                                items = uiState.continueWatching,
                                onItemClick = { /* play */ }
                            )
                        }
                    }

                    // My Favorites - Movies
                    if (uiState.favoriteMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = "My Favorites",
                                movies = uiState.favoriteMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    // My Favorites - TV Shows
                    if (uiState.favoriteTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = "Favorite Shows",
                                tvShows = uiState.favoriteTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }

                    // Public Domain Movies (always available)
                    if (uiState.publicDomainMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.public_domain_movies),
                                movies = uiState.publicDomainMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                    
                    // Popular Movies (from TMDB)
                    if (uiState.popularMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.popular_movies),
                                movies = uiState.popularMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }
                    
                    // Top Rated Movies (from TMDB)
                    if (uiState.topRatedMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.top_rated_movies),
                                movies = uiState.topRatedMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    // Popular TV Shows
                    if (uiState.popularTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = stringResource(R.string.popular_tv_shows),
                                tvShows = uiState.popularTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }

                    // Top Rated TV Shows
                    if (uiState.topRatedTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = stringResource(R.string.top_rated_tv_shows),
                                tvShows = uiState.topRatedTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }

                    // Airing Today TV Shows
                    if (uiState.airingTodayTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = stringResource(R.string.airing_today),
                                tvShows = uiState.airingTodayTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingRow(
    items: List<ContinueWatchingEntity>,
    onItemClick: (ContinueWatchingEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Continue Watching",
            modifier = Modifier.padding(start = 48.dp, bottom = 8.dp),
            color = Color.White,
            fontSize = 24.sp
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                ContinueWatchingCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContinueWatchingCard(
    item: ContinueWatchingEntity,
    onClick: () -> Unit
) {
    val progress = if (item.durationMs > 0) item.progressMs.toFloat() / item.durationMs else 0f
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = item.title,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 2
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter),
                color = Color.Red,
                trackColor = Color.DarkGray
            )
        }
    }
}

