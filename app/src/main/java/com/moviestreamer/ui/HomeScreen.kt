package com.moviestreamer.ui

import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviestreamer.BuildConfig
import com.moviestreamer.R
import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.local.UserProfileEntity
import com.moviestreamer.ui.parental.ParentalControlsManager


@androidx.media3.common.util.UnstableApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMovieClick: (Movie) -> Unit,
    onTvShowClick: (TvShow) -> Unit,
    onSearchClick: () -> Unit,
    onGenreClick: () -> Unit,
    onTorrentBrowseClick: () -> Unit,
    onContinueWatchingClick: (ContinueWatchingEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val parentalControlsManager = remember { ParentalControlsManager(context) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showProfileManager by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        if (uiState.showProfilePicker && uiState.profiles.isNotEmpty()) {
            ProfilePickerDialog(
                profiles = uiState.profiles,
                activeProfile = uiState.activeProfile,
                parentalControlsManager = parentalControlsManager,
                onDismiss = viewModel::dismissProfilePicker,
                onSelectProfile = { profileId, makeDefault -> viewModel.selectProfile(profileId, makeDefault) },
                onCreateProfile = viewModel::createProfile
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                uiState = uiState,
                parentalControlsManager = parentalControlsManager,
                onDismiss = { showSettingsDialog = false },
                onNotificationsChanged = viewModel::setNotificationsEnabled,
                onRemindersChanged = viewModel::setReminderNotificationsEnabled,
                onNewContentChanged = viewModel::setNewContentNotificationsEnabled,
                onAnalyticsChanged = viewModel::setAnalyticsEnabled
            )
        }

        if (showProfileManager) {
            ProfilePickerDialog(
                profiles = uiState.profiles,
                activeProfile = uiState.activeProfile,
                parentalControlsManager = parentalControlsManager,
                onDismiss = { showProfileManager = false },
                onSelectProfile = { profileId, makeDefault ->
                    viewModel.selectProfile(profileId, makeDefault)
                    showProfileManager = false
                },
                onCreateProfile = { name, isKids ->
                    viewModel.createProfile(name, isKids)
                    showProfileManager = false
                }
            )
        }

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
            uiState.torrentStatus != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.torrentStatus.orEmpty(),
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LinearProgressIndicator(
                        progress = uiState.torrentProgress,
                        modifier = Modifier
                            .width(320.dp)
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.stopTorrentStream() }) {
                        Text(stringResource(R.string.cancel), color = Color.White)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 32.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(start = 48.dp, bottom = 16.dp)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                color = Color.White,
                                fontSize = 48.sp
                            )
                            uiState.activeProfile?.let { profile ->
                                Text(
                                    text = stringResource(R.string.active_profile_format, profile.avatar, profile.name),
                                    color = Color.LightGray,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onTorrentBrowseClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7D46))
                            ) {
                                Text(
                                    text = stringResource(R.string.browse_torrents_action),
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            TextButton(onClick = onSearchClick) {
                                Text(stringResource(R.string.search_action), color = Color.White, fontSize = 18.sp)
                            }
                            TextButton(onClick = onGenreClick) {
                                Text(stringResource(R.string.browse_genres_action), color = Color.White, fontSize = 18.sp)
                            }
                            TextButton(onClick = { showProfileManager = true }) {
                                Text(stringResource(R.string.manage_profiles), color = Color.White, fontSize = 18.sp)
                            }
                            TextButton(onClick = { showSettingsDialog = true }) {
                                Text(stringResource(R.string.settings_title), color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }

                    if (uiState.recommendedMovies.isNotEmpty() && !uiState.recommendationsDismissed) {
                        item {
                            RecommendationsRow(
                                title = stringResource(R.string.because_you_watched),
                                subtitle = uiState.recommendationReason,
                                movies = uiState.recommendedMovies,
                                onMovieClick = onMovieClick,
                                onRefresh = viewModel::refreshRecommendations,
                                onDismiss = viewModel::dismissRecommendations
                            )
                        }
                    }

                    if (uiState.continueWatching.isNotEmpty()) {
                        item {
                            ContinueWatchingRow(
                                items = uiState.continueWatching,
                                onItemClick = onContinueWatchingClick
                            )
                        }
                    }

                    if (uiState.favoriteMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.favorite_movies),
                                movies = uiState.favoriteMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.favoriteTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = stringResource(R.string.favorite_tv_shows),
                                tvShows = uiState.favoriteTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }

                    if (uiState.publicDomainMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.public_domain_movies),
                                movies = uiState.publicDomainMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.publicDomainTvEpisodes.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.classic_tv_episodes),
                                movies = uiState.publicDomainTvEpisodes,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.freeIptvChannels.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.free_live_channels),
                                movies = uiState.freeIptvChannels,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.popularMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.popular_movies),
                                movies = uiState.popularMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.topRatedMovies.isNotEmpty()) {
                        item {
                            MovieRow(
                                title = stringResource(R.string.top_rated_movies),
                                movies = uiState.topRatedMovies,
                                onMovieClick = onMovieClick
                            )
                        }
                    }

                    if (uiState.popularTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = stringResource(R.string.popular_tv_shows),
                                tvShows = uiState.popularTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }

                    if (uiState.topRatedTvShows.isNotEmpty()) {
                        item {
                            TvShowRow(
                                title = stringResource(R.string.top_rated_tv_shows),
                                tvShows = uiState.topRatedTvShows,
                                onTvShowClick = onTvShowClick
                            )
                        }
                    }

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
private fun RecommendationsRow(
    title: String,
    subtitle: String?,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, color = Color.White, fontSize = 24.sp)
                if (!subtitle.isNullOrBlank()) {
                    Text(text = subtitle, color = Color.LightGray, fontSize = 14.sp)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onRefresh) {
                    Text(text = stringResource(R.string.refresh), color = Color.White)
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.dismiss), color = Color.White)
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(movie = movie, onMovieClick = onMovieClick)
            }
        }
    }
}

@Composable
private fun ProfilePickerDialog(
    profiles: List<UserProfileEntity>,
    activeProfile: UserProfileEntity?,
    parentalControlsManager: ParentalControlsManager,
    onDismiss: () -> Unit,
    onSelectProfile: (Long, Boolean) -> Unit,
    onCreateProfile: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    var newProfileName by remember { mutableStateOf("") }
    var createKidsProfile by remember { mutableStateOf(false) }
    var rememberAsDefault by remember { mutableStateOf(true) }
    var pendingKidsProfile by remember { mutableStateOf<UserProfileEntity?>(null) }
    var pinValue by remember { mutableStateOf("") }

    if (pendingKidsProfile != null) {
        AlertDialog(
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFE0E0E0),
            onDismissRequest = {
                pendingKidsProfile = null
                pinValue = ""
            },
            title = { Text(stringResource(R.string.enter_kids_pin)) },
            text = {
                OutlinedTextField(
                    value = pinValue,
                    onValueChange = { pinValue = it.take(8) },
                    label = { Text(stringResource(R.string.pin_label)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (parentalControlsManager.verifyPin(pinValue)) {
                        pendingKidsProfile?.let { onSelectProfile(it.id, rememberAsDefault) }
                        pendingKidsProfile = null
                        pinValue = ""
                    } else {
                        Toast.makeText(context, context.getString(R.string.invalid_pin), Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    pendingKidsProfile = null
                    pinValue = ""
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    AlertDialog(
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFE0E0E0),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.choose_profile)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                profiles.forEach { profile ->
                    val isActive = profile.id == activeProfile?.id
                    if (isActive) {
                        Button(
                            onClick = {
                                if (profile.isKids && parentalControlsManager.isEnabled && parentalControlsManager.isPinSet) {
                                    pendingKidsProfile = profile
                                } else {
                                    onSelectProfile(profile.id, rememberAsDefault)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBB86FC),
                                contentColor = Color.Black
                            )
                        ) {
                            Text(context.getString(R.string.current_profile_format, profile.avatar, profile.name))
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (profile.isKids && parentalControlsManager.isEnabled && parentalControlsManager.isPinSet) {
                                    pendingKidsProfile = profile
                                } else {
                                    onSelectProfile(profile.id, rememberAsDefault)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF6200EE))
                        ) {
                            Text(context.getString(R.string.active_profile_format, profile.avatar, profile.name))
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rememberAsDefault, onCheckedChange = { rememberAsDefault = it })
                    Text(text = stringResource(R.string.remember_default_profile), color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.add_profile), color = Color.White, fontSize = 18.sp)
                OutlinedTextField(
                    value = newProfileName,
                    onValueChange = { newProfileName = it },
                    label = { Text(stringResource(R.string.profile_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFE0E0E0),
                        focusedBorderColor = Color(0xFFBB86FC),
                        unfocusedBorderColor = Color(0xFF6200EE),
                        focusedLabelColor = Color(0xFFBB86FC),
                        unfocusedLabelColor = Color(0xFF9E9E9E),
                        cursorColor = Color(0xFFBB86FC)
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = createKidsProfile, onCheckedChange = { createKidsProfile = it })
                    Text(text = stringResource(R.string.kids_profile_toggle), color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newProfileName.isNotBlank()) {
                        onCreateProfile(newProfileName, createKidsProfile)
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                )
            ) {
                Text(if (newProfileName.isNotBlank()) stringResource(R.string.create_profile) else stringResource(R.string.done))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF6200EE))
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun SettingsDialog(
    uiState: HomeUiState,
    parentalControlsManager: ParentalControlsManager,
    onDismiss: () -> Unit,
    onNotificationsChanged: (Boolean) -> Unit,
    onRemindersChanged: (Boolean) -> Unit,
    onNewContentChanged: (Boolean) -> Unit,
    onAnalyticsChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var parentalEnabled by remember { mutableStateOf(parentalControlsManager.isEnabled) }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showAboutDialog by remember { mutableStateOf(false) }

    val darkTextFieldColors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color(0xFFE0E0E0),
        focusedBorderColor = Color(0xFFBB86FC),
        unfocusedBorderColor = Color(0xFF6200EE),
        focusedLabelColor = Color(0xFFBB86FC),
        unfocusedLabelColor = Color(0xFF9E9E9E),
        cursorColor = Color(0xFFBB86FC)
    )

    if (showAboutDialog) {
        AlertDialog(
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFE0E0E0),
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.about_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(
                            R.string.about_version_format,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE
                        ),
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.about_body),
                        color = Color(0xFFE0E0E0)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    Text(stringResource(R.string.done))
                }
            }
        )
    }

    AlertDialog(
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color(0xFFE0E0E0),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingSwitchRow(
                    title = stringResource(R.string.notifications_master_toggle),
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = onNotificationsChanged
                )
                SettingSwitchRow(
                    title = stringResource(R.string.new_content_toggle),
                    checked = uiState.newContentNotificationsEnabled,
                    onCheckedChange = onNewContentChanged
                )
                SettingSwitchRow(
                    title = stringResource(R.string.reminders_toggle),
                    checked = uiState.reminderNotificationsEnabled,
                    onCheckedChange = onRemindersChanged
                )
                SettingSwitchRow(
                    title = stringResource(R.string.analytics_toggle),
                    checked = uiState.analyticsEnabled,
                    onCheckedChange = onAnalyticsChanged
                )
                SettingSwitchRow(
                    title = stringResource(R.string.parental_controls_toggle),
                    checked = parentalEnabled,
                    onCheckedChange = {
                        parentalEnabled = it
                        parentalControlsManager.isEnabled = it
                    }
                )
                if (parentalEnabled) {
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it.take(8) },
                        label = { Text(stringResource(R.string.new_pin_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = darkTextFieldColors
                    )
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it.take(8) },
                        label = { Text(stringResource(R.string.confirm_pin_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = darkTextFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.about_section_title),
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = stringResource(
                        R.string.about_version_format,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    color = Color(0xFFE0E0E0)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showAboutDialog = true }) {
                        Text(stringResource(R.string.about_title))
                    }
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                context.getString(
                                    R.string.checking_updates,
                                    BuildConfig.VERSION_NAME,
                                    BuildConfig.VERSION_CODE
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                            uriHandler.openUri("https://github.com/Rwothoromo/movie_streamer/releases/latest")
                        }
                    ) {
                        Text(stringResource(R.string.check_for_updates))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parentalEnabled && newPin.isNotBlank()) {
                        if (newPin == confirmPin && newPin.length >= ParentalControlsManager.MIN_PIN_LENGTH) {
                            parentalControlsManager.setPin(newPin)
                            Toast.makeText(context, context.getString(R.string.pin_saved), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, context.getString(R.string.pin_mismatch), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.save_settings))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF6200EE))
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
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
            text = stringResource(R.string.continue_watching),
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

