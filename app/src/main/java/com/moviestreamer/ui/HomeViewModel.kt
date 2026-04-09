package com.moviestreamer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.data.Movie
import com.moviestreamer.data.Season
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.AppPreferencesManager
import com.moviestreamer.data.local.AppPreferencesState
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.local.UserProfileEntity
import com.moviestreamer.data.local.UserReviewEntity
import com.moviestreamer.data.repository.LocalRepository
import com.moviestreamer.domain.usecase.GetAiringTodayTvShowsUseCase
import com.moviestreamer.domain.usecase.GetContinueWatchingUseCase
import com.moviestreamer.domain.usecase.GetFavoriteMoviesUseCase
import com.moviestreamer.domain.usecase.GetFavoriteTvShowsUseCase
import com.moviestreamer.domain.usecase.GetFreeIptvChannelsUseCase
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
    val recommendedMovies: List<Movie> = emptyList(),
    val recommendationReason: String? = null,
    val recommendationsDismissed: Boolean = false,
    val popularTvShows: List<TvShow> = emptyList(),
    val topRatedTvShows: List<TvShow> = emptyList(),
    val airingTodayTvShows: List<TvShow> = emptyList(),
    val favoriteMovies: List<Movie> = emptyList(),
    val favoriteTvShows: List<TvShow> = emptyList(),
    val continueWatching: List<ContinueWatchingEntity> = emptyList(),
    val profiles: List<UserProfileEntity> = emptyList(),
    val activeProfile: UserProfileEntity? = null,
    val reviews: List<UserReviewEntity> = emptyList(),
    val showProfilePicker: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val reminderNotificationsEnabled: Boolean = true,
    val newContentNotificationsEnabled: Boolean = true,
    val analyticsEnabled: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val torrentProgress: Float = 0f,
    val torrentStatus: String? = null
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
    private val getFreeIptvChannelsUseCase: GetFreeIptvChannelsUseCase,
    private val localRepository: LocalRepository,
    private val preferencesManager: AppPreferencesManager,
    private val torrentRepository: com.moviestreamer.data.repository.TorrentRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allMovies: List<Movie> = emptyList()
    private var recommendationPool: List<Movie> = emptyList()

    init {
        viewModelScope.launch {
            localRepository.ensureDefaultProfiles()
        }
        loadContent()
        observeLocalData()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            getFavoriteMoviesUseCase().collect { movies ->
                _uiState.value = _uiState.value.copy(favoriteMovies = movies)
                recomputeRecommendations()
            }
        }
        viewModelScope.launch {
            getFavoriteTvShowsUseCase().collect { shows ->
                _uiState.value = _uiState.value.copy(favoriteTvShows = shows)
                recomputeRecommendations()
            }
        }
        viewModelScope.launch {
            getContinueWatchingUseCase().collect { items ->
                _uiState.value = _uiState.value.copy(continueWatching = items)
                recomputeRecommendations()
            }
        }
        viewModelScope.launch {
            localRepository.getProfiles().collect { profiles ->
                updateProfileState(profiles = profiles)
            }
        }
        viewModelScope.launch {
            localRepository.getAllReviews().collect { reviews ->
                _uiState.value = _uiState.value.copy(reviews = reviews)
            }
        }
        viewModelScope.launch {
            preferencesManager.state.collect { preferences ->
                updateProfileState(preferences = preferences)
                recomputeRecommendations()
            }
        }
    }

    private fun updateProfileState(
        profiles: List<UserProfileEntity> = _uiState.value.profiles,
        preferences: AppPreferencesState = preferencesManager.state.value
    ) {
        val activeProfileId = preferences.activeProfileId.takeIf { it > 0 }
            ?: preferences.defaultProfileId.takeIf { it > 0 }
        val activeProfile = profiles.firstOrNull { it.id == activeProfileId } ?: profiles.firstOrNull()

        _uiState.value = _uiState.value.copy(
            profiles = profiles,
            activeProfile = activeProfile,
            notificationsEnabled = preferences.notificationsEnabled,
            reminderNotificationsEnabled = preferences.reminderNotificationsEnabled,
            newContentNotificationsEnabled = preferences.newContentNotificationsEnabled,
            analyticsEnabled = preferences.analyticsEnabled
        )
    }

    private fun loadContent() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val publicDomain = getPublicDomainMoviesUseCase()
                val publicDomainTvEpisodes = getPublicDomainTvEpisodesUseCase()
                val freeIptvChannels = getFreeIptvChannelsUseCase()
                val popular = getPopularMoviesUseCase().getOrDefault(emptyList())
                val topRated = getTopRatedMoviesUseCase().getOrDefault(emptyList())
                val popularTv = getPopularTvShowsUseCase().getOrDefault(emptyList())
                val topRatedTv = getTopRatedTvShowsUseCase().getOrDefault(emptyList())
                val airingTodayTv = getAiringTodayTvShowsUseCase().getOrDefault(emptyList())
                allMovies = (popular + topRated + publicDomain + publicDomainTvEpisodes + freeIptvChannels)
                    .distinctBy { it.id }
                _uiState.value = _uiState.value.copy(
                    publicDomainMovies = publicDomain,
                    publicDomainTvEpisodes = publicDomainTvEpisodes,
                    freeIptvChannels = freeIptvChannels,
                    popularMovies = popular,
                    topRatedMovies = topRated,
                    popularTvShows = popularTv,
                    topRatedTvShows = topRatedTv,
                    airingTodayTvShows = airingTodayTv,
                    isLoading = false,
                    error = null
                )
                recomputeRecommendations()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun recomputeRecommendations() {
        val state = _uiState.value
        val favoriteIds = state.favoriteMovies.map { it.id }.toSet()
        val continueWatchingTitles = state.continueWatching.map { it.title.lowercase() }.toSet()

        val seeds = buildList {
            addAll(state.favoriteMovies.mapNotNull { favorite -> allMovies.find { it.id == favorite.id } })
            addAll(state.continueWatching.mapNotNull { item ->
                allMovies.find { it.title.equals(item.title, ignoreCase = true) }
            })
        }

        val favoriteTvSeed = state.favoriteTvShows.firstOrNull()?.name
        val seedGenres = seeds.flatMap { it.genreIds }.groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }
            .map { it.key }
            .toSet()

        recommendationPool = allMovies
            .filterNot { candidate ->
                candidate.id in favoriteIds || candidate.title.lowercase() in continueWatchingTitles
            }
            .distinctBy { it.id }
            .sortedWith(
                compareByDescending<Movie> { candidate ->
                    candidate.genreIds.count { it in seedGenres }
                }.thenByDescending { it.voteAverage ?: 0.0 }
            )

        val recommendations = when {
            recommendationPool.isNotEmpty() -> recommendationPool.take(10)
            allMovies.isNotEmpty() -> allMovies.shuffled().take(10)
            else -> emptyList()
        }

        val reason = when {
            seeds.isNotEmpty() -> "Because you watched ${seeds.first().title}"
            !favoriteTvSeed.isNullOrBlank() -> "Suggested for fans of $favoriteTvSeed"
            state.activeProfile != null -> "Trending picks for ${state.activeProfile.name}"
            else -> "Trending picks for you"
        }

        _uiState.value = _uiState.value.copy(
            recommendedMovies = recommendations,
            recommendationReason = reason
        )
    }

    fun refreshRecommendations() {
        val refreshed = if (recommendationPool.isNotEmpty()) {
            recommendationPool.shuffled().take(10)
        } else {
            allMovies.shuffled().take(10)
        }
        _uiState.value = _uiState.value.copy(
            recommendedMovies = refreshed,
            recommendationsDismissed = false
        )
    }

    fun dismissRecommendations() {
        _uiState.value = _uiState.value.copy(recommendationsDismissed = true)
    }

    fun retry() {
        loadContent()
    }

    fun dismissProfilePicker() {
        _uiState.value = _uiState.value.copy(showProfilePicker = false)
    }

    fun reopenProfilePicker() {
        _uiState.value = _uiState.value.copy(showProfilePicker = true)
    }

    fun selectProfile(profileId: Long, makeDefault: Boolean = false) {
        preferencesManager.setActiveProfile(profileId, makeDefault)
        _uiState.value = _uiState.value.copy(
            showProfilePicker = false,
            recommendationsDismissed = false
        )
    }

    fun createProfile(name: String, isKids: Boolean) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val avatar = if (isKids) "🧒" else listOf("🎬", "🍿", "📺", "⭐").random()
            val profileId = localRepository.createProfile(name.trim(), avatar, isKids)
            preferencesManager.setActiveProfile(profileId)
            _uiState.value = _uiState.value.copy(showProfilePicker = false)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferencesManager.setNotificationsEnabled(enabled)
    }

    fun setReminderNotificationsEnabled(enabled: Boolean) {
        preferencesManager.setReminderNotificationsEnabled(enabled)
    }

    fun setNewContentNotificationsEnabled(enabled: Boolean) {
        preferencesManager.setNewContentNotificationsEnabled(enabled)
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        preferencesManager.setAnalyticsEnabled(enabled)
    }

    fun saveMovieReview(movie: Movie, rating: Int, review: String) {
        saveReview(
            contentId = movie.id.toString(),
            contentType = CONTENT_TYPE_MOVIE,
            title = movie.title,
            posterPath = movie.posterPath,
            rating = rating,
            review = review
        )
    }

    fun saveTvReview(tvShow: TvShow, rating: Int, review: String) {
        saveReview(
            contentId = tvShow.id.toString(),
            contentType = CONTENT_TYPE_TV,
            title = tvShow.name,
            posterPath = tvShow.posterPath,
            rating = rating,
            review = review
        )
    }

    private fun saveReview(
        contentId: String,
        contentType: String,
        title: String,
        posterPath: String?,
        rating: Int,
        review: String
    ) {
        viewModelScope.launch {
            localRepository.saveReview(
                contentId = contentId,
                contentType = contentType,
                title = title,
                posterPath = posterPath,
                rating = rating,
                review = review
            )
        }
    }

    fun getReviewsForContent(contentId: String, contentType: String): List<UserReviewEntity> =
        _uiState.value.reviews.filter {
            it.contentId == contentId && it.contentType == contentType
        }

    fun getAverageRatingForContent(contentId: String, contentType: String): Double? {
        val ratings = getReviewsForContent(contentId, contentType).map { it.rating }
        return if (ratings.isEmpty()) null else ratings.average()
    }

    fun getUserReviewForContent(contentId: String, contentType: String): UserReviewEntity? {
        val activeProfileId = _uiState.value.activeProfile?.id ?: return null
        return _uiState.value.reviews.firstOrNull {
            it.profileId == activeProfileId &&
                it.contentId == contentId &&
                it.contentType == contentType
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            searchResults = searchMoviesUseCase(query, allMovies)
        )
    }

    fun startTorrentStream(magnetUri: String, downloadDir: java.io.File, onReady: (file: java.io.File?) -> Unit) {
        _uiState.value = _uiState.value.copy(torrentProgress = 0f, torrentStatus = "Starting torrent...")
        torrentRepository.startTorrentStream(magnetUri, downloadDir) { file, progress, status ->
            _uiState.value = _uiState.value.copy(
                torrentProgress = progress,
                torrentStatus = status
            )
            if (file != null && progress >= 1f) {
                _uiState.value = _uiState.value.copy(torrentStatus = "Ready to play")
                onReady(file)
            }
        }
    }

    fun stopTorrentStream() {
        torrentRepository.stopTorrentStream()
        _uiState.value = _uiState.value.copy(torrentProgress = 0f, torrentStatus = null)
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

    companion object {
        const val CONTENT_TYPE_MOVIE = "movie"
        const val CONTENT_TYPE_TV = "tv"
    }
}
