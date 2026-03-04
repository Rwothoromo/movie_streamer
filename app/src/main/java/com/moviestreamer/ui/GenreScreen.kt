package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreScreen(
    viewModel: GenreViewModel,
    onMovieClick: (Movie) -> Unit,
    onTvShowClick: (TvShow) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("← Back", color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Browse by Genre", color = Color.White, fontSize = 36.sp)
                }
            }

            // Tab row
            item {
                Row(modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)) {
                    ContentTab.entries.forEach { tab ->
                        val label = if (tab == ContentTab.MOVIES) "Movies" else "TV Shows"
                        val selected = uiState.selectedTab == tab
                        Button(
                            onClick = { viewModel.selectTab(tab) },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color.White else Color.DarkGray,
                                contentColor = if (selected) Color.Black else Color.White
                            )
                        ) {
                            Text(label)
                        }
                    }
                }
            }

            // Genre chips
            item {
                val genres = if (uiState.selectedTab == ContentTab.MOVIES) {
                    uiState.movieGenres
                } else {
                    uiState.tvGenres
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        val selected = uiState.selectedGenre?.id == genre.id
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.selectGenre(genre) },
                            label = { Text(genre.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White,
                                selectedLabelColor = Color.Black,
                                containerColor = Color.DarkGray,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Results
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.selectedGenre != null) {
                when (uiState.selectedTab) {
                    ContentTab.MOVIES -> {
                        if (uiState.movieResults.isNotEmpty()) {
                            item {
                                MovieRow(
                                    title = uiState.selectedGenre!!.name,
                                    movies = uiState.movieResults,
                                    onMovieClick = onMovieClick
                                )
                            }
                        } else {
                            item {
                                Text(
                                    "No movies found for ${uiState.selectedGenre!!.name}",
                                    modifier = Modifier.padding(start = 48.dp, top = 16.dp),
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                    ContentTab.TV_SHOWS -> {
                        if (uiState.tvShowResults.isNotEmpty()) {
                            item {
                                TvShowRow(
                                    title = uiState.selectedGenre!!.name,
                                    tvShows = uiState.tvShowResults,
                                    onTvShowClick = onTvShowClick
                                )
                            }
                        } else {
                            item {
                                Text(
                                    "No TV shows found for ${uiState.selectedGenre!!.name}",
                                    modifier = Modifier.padding(start = 48.dp, top = 16.dp),
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
