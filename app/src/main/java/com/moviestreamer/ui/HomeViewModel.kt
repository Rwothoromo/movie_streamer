package com.moviestreamer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.data.Movie
import com.moviestreamer.data.Season
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.domain.usecase.GetAiringTodayTvShowsUseCase
import com.moviestreamer.domain.usecase.GetContinueWatchingUseCase
import com.moviestreamer.domain.usecase.GetFavoriteMoviesUseCase
import com.moviestreamer.domain.usecase.GetFavoriteTvShowsUseCase
import com.moviestreamer.domain.usecase.GetFreeIptvChannelsUseCase
import com.moviestreamer.domain.usecase.GetMoviesByGenreUseCase
import com.moviestreamer.domain.usecase.GetPopularMoviesUseCase
import com.moviestreamer.domain.usecase.GetPopularTvShowsUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainMoviesUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainTvEpisodesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedTvShowsUseCase
import com.moviestreamer.domain.usecase.GetTvSeasonDetailsUseCase
import com.moviestreamer.domain.usecase.GetTvShowDetailsUseCase
import com.moviestreamer.domain.usecase.SearchMoviesUseCase
import com.moviestreamer.domain.usecase.ToggleFavoriteMovieUseCase
import com.moviestreamer.domain.usecase.ToggleFavoriteTvShowUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val publicDomainMovies: List<Movie> = emptyList(),
    val publicDomainTvEpisodes: List<Movie> = emptyList(),
    val freeIptvChannels: List<Movie> = emptyList(),
    val popularTvShows: List<TvShow> = emptyList(),
    val topRatedTvShows: List<TvShow> = emptyList(),
    val airingTodayTvShows: List<TvShow> = emptyList(),
    val favoriteMovies: List<Movie> = emptyList(),
    val favoriteTvShows: List<TvShow> = emptyList(),
    val continueWatching: List<ContinueWatchingEntity> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getPublicDomainMoviesUseCase: GetPublicDomainMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase,
    private val getTopRatedTvShowsUseCase: GetTopRatedTvShowsUseCase,
    private val getAiringTodayTvShowsUseCase: GetAiringTodayTvShowsUseCase,
    private val getTvShowDetailsUseCase: GetTvShowDetailsUseCase,
    private val getTvSeasonDetailsUseCase: GetTvSeasonDetailsUseCase,
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase,
    private val getFavoriteTvShowsUseCase: GetFavoriteTvShowsUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val toggleFavoriteTvShowUseCase: ToggleFavoriteTvShowUseCase,
    private val getContinueWatchingUseCase: GetContinueWatchingUseCase,
    private val getPublicDomainTvEpisodesUseCase: GetPublicDomainTvEpisodesUseCase,
    private val getFreeIptvChannelsUseCase: GetFreeIptvChannelsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allMovies: List<Movie> = emptyList()

    init {
        loadContent()
        observeLocalData()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            getFavoriteMoviesUseCase().collect { movies ->
                _uiState.value = _uiState.value.copy(favoriteMovies = movies)
            }
        }
        viewModelScope.launch {
            getFavoriteTvShowsUseCase().collect { shows ->
                _uiState.value = _uiState.value.copy(favoriteTvShows = shows)
            }
        }
        viewModelScope.launch {
            getContinueWatchingUseCase().collect { items ->
                _uiState.value = _uiState.value.copy(continueWatching = items)
            }
        }
    }

    private fun loadContent() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val publicDomain = getPublicDomainMoviesUseCase()
                val popular = getPopularMoviesUseCase().getOrDefault(emptyList())
                val topRated = getTopRatedMoviesUseCase().getOrDefault(emptyList())
                val popularTv = getPopularTvShowsUseCase().getOrDefault(emptyList())
                val topRatedTv = getTopRatedTvShowsUseCase().getOrDefault(emptyList())
                val airingToday = getAiringTodayTvShowsUseCase().getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    popularMovies = popular,
                    topRatedMovies = topRated,
                    publicDomainMovies = publicDomain,
                    publicDomainTvEpisodes = getPublicDomainTvEpisodesUseCase(),
                    freeIptvChannels = getFreeIptvChannelsUseCase(),
                    popularTvShows = popularTv,
                    topRatedTvShows = topRatedTv,
                    airingTodayTvShows = airingToday,
                    isLoading = false
                )
                allMovies = popular + topRated + publicDomain
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun retry() {
        loadContent()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _uiState.value = _uiState.value.copy(
            searchResults = searchMoviesUseCase(query, allMovies)
        )
    }

    fun toggleFavoriteMovie(movie: Movie) {
        viewModelScope.launch { toggleFavoriteMovieUseCase(movie) }
    }

    fun toggleFavoriteTvShow(tvShow: TvShow) {
        viewModelScope.launch { toggleFavoriteTvShowUseCase(tvShow) }
    }

    fun getTvSeasonDetails(tvId: Int, seasonNumber: Int, onResult: (Season?) -> Unit) {
        viewModelScope.launch {
            val result = getTvSeasonDetailsUseCase(tvId, seasonNumber)
            onResult(result.getOrNull())
        }
    }

    /**
     * Called when the app is opened via a deep link containing a movie id.
     * Sets the search query so the matching movie surfaces in the UI.
     */
    fun requestMovieById(movieId: Int) {
        val movie = allMovies.find { it.id == movieId } ?: return
        _uiState.value = _uiState.value.copy(
            searchQuery = movie.title,
            searchResults = listOf(movie)
        )
    }
}
