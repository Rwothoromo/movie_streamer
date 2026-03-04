package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun SearchScreen(
    viewModel: SearchViewModel,
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
                    Text("Search", color = Color.White, fontSize = 36.sp)
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { viewModel.updateQuery(it) },
                    placeholder = { Text("Search movies and TV shows...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 8.dp),
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
            } else if (uiState.query.isNotBlank()) {
                if (uiState.movieResults.isNotEmpty()) {
                    item {
                        MovieRow(
                            title = "Movies",
                            movies = uiState.movieResults,
                            onMovieClick = onMovieClick
                        )
                    }
                }
                if (uiState.tvShowResults.isNotEmpty()) {
                    item {
                        TvShowRow(
                            title = "TV Shows",
                            tvShows = uiState.tvShowResults,
                            onTvShowClick = onTvShowClick
                        )
                    }
                }
                if (uiState.movieResults.isEmpty() && uiState.tvShowResults.isEmpty()) {
                    item {
                        Text(
                            text = "No results found for \"${uiState.query}\"",
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
