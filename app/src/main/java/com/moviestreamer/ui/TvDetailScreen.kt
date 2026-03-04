package com.moviestreamer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.moviestreamer.R
import com.moviestreamer.data.TvShow

@Composable
fun TvDetailScreen(
    tvShow: TvShow,
    onBack: () -> Unit,
    onSeasonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var overviewExpanded by remember { mutableStateOf(false) }
    val overviewCollapseThreshold = 200

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // Backdrop image with back button overlay
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (tvShow.getBackdropUrl() != null) {
                    AsyncImage(
                        model = tvShow.getBackdropUrl(),
                        contentDescription = tvShow.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.default_movie_thumbnail),
                        contentDescription = tvShow.name,
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
                        contentDescription = stringResource(R.string.tv_detail_back),
                        tint = Color.White
                    )
                }
            }
        }

        // Poster + metadata row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (tvShow.getPosterUrl() != null) {
                    AsyncImage(
                        model = tvShow.getPosterUrl(),
                        contentDescription = tvShow.name,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.default_movie_thumbnail),
                        contentDescription = tvShow.name,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tvShow.name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (!tvShow.firstAirDate.isNullOrBlank()) {
                        Text(
                            text = stringResource(R.string.first_air_date, tvShow.firstAirDate),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    if (tvShow.voteAverage != null) {
                        Text(
                            text = stringResource(R.string.vote_average_format, tvShow.voteAverage),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Overview
        if (!tvShow.overview.isNullOrBlank()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = tvShow.overview,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (overviewExpanded) Int.MAX_VALUE else 6,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (tvShow.overview.length > overviewCollapseThreshold) {
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

        // Seasons list (placeholder — shows 1..5 if no season data available)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(R.string.seasons),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed((1..5).toList()) { _, seasonNumber ->
                        SeasonButton(
                            seasonNumber = seasonNumber,
                            onClick = { onSeasonClick(seasonNumber) }
                        )
                    }
                }
            }
        }

        // Similar Shows placeholder
        item {
            TvShowRow(
                title = stringResource(R.string.similar_shows),
                tvShows = emptyList(),
                onTvShowClick = {}
            )
        }
    }
}

@Composable
private fun SeasonButton(
    seasonNumber: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color.White else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.season_number, seasonNumber),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
