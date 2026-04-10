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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val cardScale by animateFloatAsState(targetValue = if (isFocused) 1.06f else 1f, label = "card_scale")
    val cardElevation by animateDpAsState(targetValue = if (isFocused) 12.dp else 2.dp, label = "card_elevation")
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
            .scale(cardScale)
            .border(
                border = if (isFocused) {
                    BorderStroke(4.dp, focusColor)
                } else {
                    BorderStroke(0.dp, Color.Transparent)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .focusable()
            .clickable { onMovieClick(movie) }
            .semantics {
                role = Role.Button
                contentDescription = cardContentDesc
            },
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
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
                val imageUrl = movie.getPosterUrl() ?: movie.getBackdropUrl()
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = stringResource(R.string.movie_poster_desc, movie.title),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.default_movie_thumbnail)
                    )
                } else {
                    val placeholderColors = listOf(
                        Color(0xFF1A237E), Color(0xFF4A148C), Color(0xFF880E4F),
                        Color(0xFF1B5E20), Color(0xFF0D47A1), Color(0xFF4E342E),
                        Color(0xFF37474F), Color(0xFF006064)
                    )
                    val bgColor = placeholderColors[
                        (movie.title.hashCode() and 0x7FFFFFFF) % placeholderColors.size
                    ]
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(listOf(bgColor, Color(0xFF0A0A0A)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = movie.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
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
