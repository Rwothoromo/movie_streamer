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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moviestreamer.R
import com.moviestreamer.data.TvShow

@Composable
fun TvShowCard(
    tvShow: TvShow,
    onTvShowClick: (TvShow) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusColor = colorResource(R.color.focus_highlight)
    val surfaceColor = colorResource(R.color.surface)
    val cardScale by animateFloatAsState(targetValue = if (isFocused) 1.06f else 1f, label = "card_scale")
    val cardElevation by animateDpAsState(targetValue = if (isFocused) 12.dp else 2.dp, label = "card_elevation")

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
            .clickable { onTvShowClick(tvShow) }
            .semantics {
                role = Role.Button
            },
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                val imageUrl = tvShow.getPosterUrl() ?: tvShow.getBackdropUrl()
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = tvShow.name,
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
                        (tvShow.name.hashCode() and 0x7FFFFFFF) % placeholderColors.size
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
                            text = tvShow.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

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
                        contentDescription = stringResource(R.string.browse_only_show),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = tvShow.name,
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
fun TvShowRow(
    title: String,
    tvShows: List<TvShow>,
    onTvShowClick: (TvShow) -> Unit,
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
            items(tvShows, key = { it.id }) { tvShow ->
                TvShowCard(
                    tvShow = tvShow,
                    onTvShowClick = onTvShowClick
                )
            }
        }
    }
}
