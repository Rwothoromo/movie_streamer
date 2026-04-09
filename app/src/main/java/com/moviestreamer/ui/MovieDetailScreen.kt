package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.moviestreamer.R
import com.moviestreamer.data.Movie
import com.moviestreamer.data.local.UserReviewEntity

@Composable
fun MovieDetailScreen(
    viewModel: HomeViewModel,
    movie: Movie,
    onBack: () -> Unit,
    onPlayMovie: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var overviewExpanded by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    val reviews = viewModel.getReviewsForContent(movie.id.toString(), HomeViewModel.CONTENT_TYPE_MOVIE)
    val averageRating = viewModel.getAverageRatingForContent(movie.id.toString(), HomeViewModel.CONTENT_TYPE_MOVIE)
    val existingReview = viewModel.getUserReviewForContent(movie.id.toString(), HomeViewModel.CONTENT_TYPE_MOVIE)
    val isFavorite = uiState.favoriteMovies.any { it.id == movie.id }

    if (showReviewDialog) {
        ReviewEditDialog(
            title = movie.title,
            initialRating = existingReview?.rating ?: 0,
            initialReview = existingReview?.review.orEmpty(),
            onDismiss = { showReviewDialog = false },
            onSave = { rating, review ->
                viewModel.saveMovieReview(movie, rating, review)
                showReviewDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (movie.getBackdropUrl() != null) {
                    AsyncImage(
                        model = movie.getBackdropUrl(),
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.default_movie_thumbnail),
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = Color.White
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (movie.getPosterUrl() != null) {
                    AsyncImage(
                        model = movie.getPosterUrl(),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.default_movie_thumbnail),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (!movie.releaseDate.isNullOrBlank()) {
                        Text(
                            text = movie.releaseDate,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (movie.voteAverage != null) {
                        Text(
                            text = stringResource(R.string.vote_average_format, movie.voteAverage),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    averageRating?.let {
                        Text(
                            text = stringResource(R.string.community_rating_format, it),
                            color = Color(0xFFFFC107),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (movie.videoUrl != null) {
                            Button(onClick = { onPlayMovie(movie) }) {
                                Text(text = stringResource(R.string.play))
                            }
                        }
                        OutlinedButton(onClick = { viewModel.toggleFavoriteMovie(movie) }) {
                            Text(if (isFavorite) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite))
                        }
                        OutlinedButton(onClick = { showReviewDialog = true }) {
                            Text(stringResource(R.string.rate_review))
                        }
                    }
                }
            }
        }

        if (!movie.overview.isNullOrBlank()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = movie.overview,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (overviewExpanded) Int.MAX_VALUE else 6,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (movie.overview.length > 200) {
                        TextButton(onClick = { overviewExpanded = !overviewExpanded }) {
                            Text(
                                text = if (overviewExpanded) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item {
            ContentReviewSection(
                averageRating = averageRating,
                reviews = reviews,
                onAddReview = { showReviewDialog = true },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ContentReviewSection(
    averageRating: Double?,
    reviews: List<UserReviewEntity>,
    onAddReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ratings_reviews),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onAddReview) {
                Text(text = stringResource(R.string.rate_review))
            }
        }

        averageRating?.let {
            Text(
                text = stringResource(R.string.community_rating_format, it),
                color = Color(0xFFFFC107),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.no_reviews_yet),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            reviews.take(3).forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "${review.rating}/5 ★",
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (review.review.isNotBlank()) {
                            Text(
                                text = review.review,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewEditDialog(
    title: String,
    initialRating: Int,
    initialReview: String,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(initialRating.coerceIn(0, 5)) }
    var review by remember { mutableStateOf(initialReview) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.rate_this_title, title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StarRatingPicker(
                    rating = rating,
                    onRatingSelected = { rating = it }
                )
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text(stringResource(R.string.review_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(rating.coerceAtLeast(1), review.trim()) }) {
                Text(stringResource(R.string.save_review))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun StarRatingPicker(
    rating: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (1..5).forEach { star ->
            TextButton(onClick = { onRatingSelected(star) }) {
                Text(
                    text = if (star <= rating) "★" else "☆",
                    color = if (star <= rating) Color(0xFFFFC107) else Color.LightGray,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
