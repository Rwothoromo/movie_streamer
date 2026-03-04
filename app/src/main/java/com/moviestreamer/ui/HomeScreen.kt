package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Movie) -> Unit,
    onTvShowClick: (TvShow) -> Unit,
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
                    
                    // Search Bar
                    item {
                        SearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
                        )
                    }
                    
                    // Show search results if there's a search query
                    if (uiState.searchQuery.isNotBlank()) {
                        if (uiState.searchResults.isNotEmpty()) {
                            item {
                                MovieRow(
                                    title = stringResource(R.string.search_results),
                                    movies = uiState.searchResults,
                                    onMovieClick = onMovieClick
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = stringResource(R.string.no_search_results),
                                    modifier = Modifier.padding(start = 48.dp, top = 16.dp),
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    } else {
                        // Normal browse view
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_hint),
                color = Color.Gray
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.White
        ),
        singleLine = true
    )
}
