package com.moviestreamer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moviestreamer.R
import com.moviestreamer.data.Movie
import com.moviestreamer.download.DownloadStatus
import com.moviestreamer.download.MovieDownloadManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import org.koin.androidx.compose.get
import com.moviestreamer.ui.MovieDownloadViewModel

@Composable
fun MovieCard(
    movie: Movie,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    downloadManager: MovieDownloadManager = get()
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusColor = colorResource(R.color.focus_highlight)
    val surfaceColor = colorResource(R.color.surface)
    val playableDesc = stringResource(R.string.play_movie_desc, movie.title)
    val browseDesc = stringResource(R.string.movie_poster_desc, movie.title)
    val cardContentDesc = if (movie.videoUrl != null) playableDesc else browseDesc

    // Download state per card
    val downloadViewModel = remember(movie.id) {
        MovieDownloadViewModel(movie, downloadManager)
    }
    val downloadUiState by downloadViewModel.uiState.collectAsState()

    Card(
        modifier = modifier
            .width(200.dp)
            .height(300.dp)
            .padding(8.dp)
            .border(
                border = if (isFocused) {
                    BorderStroke(4.dp, focusColor)
                } else {
                    BorderStroke(0.dp, Color.Transparent)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .focusable()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .clickable { onMovieClick(movie) }
            .semantics {
                role = Role.Button
                contentDescription = cardContentDesc
            },
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            // Movie poster
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                if (movie.getPosterUrl() != null) {
                    AsyncImage(
                        model = movie.getPosterUrl(),
                        contentDescription = stringResource(R.string.movie_poster_desc, movie.title),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default movie thumbnail for movies without poster
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.default_movie_thumbnail),
                        contentDescription = stringResource(R.string.movie_poster_desc, movie.title),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Download/Play/Progress UI for public domain movies
                if (movie.videoUrl != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(40.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val status = downloadUiState.status) {
                            is DownloadStatus.Success -> {
                                IconButton(onClick = { /* Play downloaded movie (handled by onMovieClick) */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Downloaded",
                                        tint = Color.Green,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                IconButton(onClick = { downloadViewModel.removeDownload() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Remove Download",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            is DownloadStatus.Progress -> {
                                CircularProgressIndicator(
                                    progress = if (status.bytesTotal > 0) status.bytesDownloaded / status.bytesTotal.toFloat() else 0f,
                                    color = Color.Yellow,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                            is DownloadStatus.Failed -> {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = "Download Failed",
                                    tint = Color.Red,
                                    modifier = Modifier.size(24.dp)
                                )
                                IconButton(onClick = { downloadViewModel.startDownload() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Download,
                                        contentDescription = "Retry Download",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            else -> {
                                IconButton(onClick = { downloadViewModel.startDownload() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Download,
                                        contentDescription = "Download",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        // Show error message if present
                        downloadUiState.errorMessage?.let { msg ->
                            Text(
                                text = msg,
                                color = Color.Red,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 2.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.browse_only_movie),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // Movie title
            Text(
                text = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MovieRow(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 48.dp, bottom = 8.dp),
            color = Color.White,
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}
