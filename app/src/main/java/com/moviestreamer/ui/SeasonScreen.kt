package com.moviestreamer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.moviestreamer.data.Episode
import com.moviestreamer.data.Season
import com.moviestreamer.data.TvShow

@Composable
fun SeasonScreen(
    tvShow: TvShow,
    season: Season?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.tv_detail_back),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = tvShow.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                val seasonLabel = if (!season?.name.isNullOrBlank()) {
                    season!!.name!!
                } else if (season != null) {
                    stringResource(R.string.season_number, season.seasonNumber)
                } else {
                    ""
                }
                if (seasonLabel.isNotBlank()) {
                    Text(
                        text = seasonLabel,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            season == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.error_loading),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                val episodes = season.episodes ?: emptyList()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(episodes, key = { it.id }) { episode ->
                        EpisodeCard(
                            episode = episode,
                            onClick = onEpisodeClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeCard(
    episode: Episode,
    onClick: (Episode) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                border = if (isFocused) BorderStroke(2.dp, Color.White) else BorderStroke(0.dp, Color.Transparent),
                shape = RoundedCornerShape(8.dp)
            )
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick(episode) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (episode.getStillUrl() != null) {
                AsyncImage(
                    model = episode.getStillUrl(),
                    contentDescription = episode.name,
                    modifier = Modifier
                        .width(160.dp)
                        .height(90.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.default_movie_thumbnail),
                    contentDescription = episode.name,
                    modifier = Modifier
                        .width(160.dp)
                        .height(90.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.episode_number, episode.episodeNumber) +
                        if (!episode.name.isNullOrBlank()) " · ${episode.name}" else "",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!episode.airDate.isNullOrBlank()) {
                    Text(
                        text = episode.airDate,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                if (episode.voteAverage != null && episode.voteAverage > 0) {
                    Text(
                        text = stringResource(R.string.vote_average_format, episode.voteAverage),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                if (!episode.overview.isNullOrBlank()) {
                    Text(
                        text = episode.overview,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
