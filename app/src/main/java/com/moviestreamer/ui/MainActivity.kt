package com.moviestreamer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.moviestreamer.BuildConfig
import com.moviestreamer.data.Movie
import com.moviestreamer.data.Season
import com.moviestreamer.data.TvShow
import com.moviestreamer.player.PlayerActivity
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class Screen {
    object Home : Screen()
    object Search : Screen()
    object Genre : Screen()
    object TorrentBrowser : Screen()
    data class MovieDetail(val movie: Movie) : Screen()
    data class TvDetail(val tvShow: TvShow) : Screen()
    data class TvSeason(val tvShow: TvShow, val seasonNumber: Int, val season: Season?) : Screen()
}

@androidx.media3.common.util.UnstableApi
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Warning about alpha library usage
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "This app uses Compose for TV libraries in alpha stage (1.0.0-alpha10). " +
                    "Alpha libraries may have breaking API changes or stability issues. " +
                    "Monitor https://developer.android.com/jetpack/androidx/releases/tv for stable releases.")
        }

        // Handle deep links: moviestreamer://movie/{id} or https://moviestreamer.app/movie/{id}
        handleDeepLink(intent)
        
        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            var seasonLoading by remember { mutableStateOf(false) }
            val searchViewModel: SearchViewModel = koinViewModel()
            val genreViewModel: GenreViewModel = koinViewModel()

            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onMovieClick = { movie ->
                            currentScreen = Screen.MovieDetail(movie)
                        },
                        onTvShowClick = { tvShow ->
                            currentScreen = Screen.TvDetail(tvShow)
                        },
                        onSearchClick = { currentScreen = Screen.Search },
                        onGenreClick = { currentScreen = Screen.Genre },
                        onTorrentBrowseClick = { currentScreen = Screen.TorrentBrowser },
                        onContinueWatchingClick = { item ->
                            val intent = Intent(this, PlayerActivity::class.java).apply {
                                putExtra(PlayerActivity.EXTRA_VIDEO_URL, item.videoUrl)
                                putExtra(PlayerActivity.EXTRA_MOVIE_TITLE, item.title)
                                putExtra(PlayerActivity.EXTRA_CONTENT_ID, item.contentId)
                            }
                            startActivity(intent)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.Search -> {
                    SearchScreen(
                        viewModel = searchViewModel,
                        onMovieClick = { movie -> currentScreen = Screen.MovieDetail(movie) },
                        onTvShowClick = { tvShow -> currentScreen = Screen.TvDetail(tvShow) },
                        onBack = { currentScreen = Screen.Home },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.Genre -> {
                    GenreScreen(
                        viewModel = genreViewModel,
                        onMovieClick = { movie -> currentScreen = Screen.MovieDetail(movie) },
                        onTvShowClick = { tvShow -> currentScreen = Screen.TvDetail(tvShow) },
                        onBack = { currentScreen = Screen.Home },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.TorrentBrowser -> {
                    TorrentBrowserScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = Screen.Home },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.MovieDetail -> {
                    MovieDetailScreen(
                        viewModel = viewModel,
                        movie = screen.movie,
                        onBack = { currentScreen = Screen.Home },
                        onPlayMovie = { movie ->
                            if (movie.videoUrl != null) {
                                val intent = Intent(this, PlayerActivity::class.java).apply {
                                    putExtra(PlayerActivity.EXTRA_VIDEO_URL, movie.videoUrl)
                                    putExtra(PlayerActivity.EXTRA_MOVIE_TITLE, movie.title)
                                    putExtra(PlayerActivity.EXTRA_SUBTITLE_URL, movie.subtitleUrl)
                                    putExtra(PlayerActivity.EXTRA_CONTENT_ID, movie.id.toString())
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Streaming not available for this title", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.TvDetail -> {
                    TvDetailScreen(
                        viewModel = viewModel,
                        tvShow = screen.tvShow,
                        onBack = { currentScreen = Screen.Home },
                        onSeasonClick = { seasonNumber ->
                            seasonLoading = true
                            currentScreen = Screen.TvSeason(screen.tvShow, seasonNumber, null)
                            viewModel.getTvSeasonDetails(screen.tvShow.id, seasonNumber) { season ->
                                seasonLoading = false
                                currentScreen = Screen.TvSeason(screen.tvShow, seasonNumber, season)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Screen.TvSeason -> {
                    SeasonScreen(
                        tvShow = screen.tvShow,
                        season = screen.season,
                        isLoading = seasonLoading,
                        onBack = { currentScreen = Screen.TvDetail(screen.tvShow) },
                        onEpisodeClick = { episode ->
                            Log.i(TAG, "Episode clicked: ${episode.name}")
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    
    companion object {
        private const val TAG = "MainActivity"
    }

    // ── Deep link handling ────────────────────────────────────────────────────

    private fun handleDeepLink(intent: Intent?) {
        val uri: Uri = intent?.data ?: return
        val movieId = extractMovieId(uri) ?: return
        // viewModel will load content and the movie with this id can be highlighted/auto-played.
        // For now log the intent and let the home screen handle it via state.
        Log.i(TAG, "Deep link received for movie id: $movieId")
        viewModel.requestMovieById(movieId)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun extractMovieId(uri: Uri): Int? {
        // Handles:
        //   moviestreamer://movie/12345
        //   https://moviestreamer.app/movie/12345
        val segments = uri.pathSegments
        return when {
            uri.scheme == "moviestreamer" -> uri.host?.toIntOrNull()
                ?: segments.firstOrNull()?.toIntOrNull()
            segments.size >= 2 && segments[segments.size - 2] == "movie" ->
                segments.last().toIntOrNull()
            else -> null
        }
    }
}
