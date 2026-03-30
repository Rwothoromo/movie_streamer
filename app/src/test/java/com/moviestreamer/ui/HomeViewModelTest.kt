package com.moviestreamer.ui

import app.cash.turbine.test
import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.domain.usecase.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    // Use cases
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase = mockk()
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase = mockk()
    private val getPublicDomainMoviesUseCase: GetPublicDomainMoviesUseCase = mockk()
    private val searchMoviesUseCase: SearchMoviesUseCase = mockk()
    private val getPopularTvShowsUseCase: GetPopularTvShowsUseCase = mockk()
    private val getTopRatedTvShowsUseCase: GetTopRatedTvShowsUseCase = mockk()
    private val getAiringTodayTvShowsUseCase: GetAiringTodayTvShowsUseCase = mockk()
    private val getTvShowDetailsUseCase: GetTvShowDetailsUseCase = mockk()
    private val getTvSeasonDetailsUseCase: GetTvSeasonDetailsUseCase = mockk()
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase = mockk()
    private val getFavoriteTvShowsUseCase: GetFavoriteTvShowsUseCase = mockk()
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase = mockk()
    private val toggleFavoriteTvShowUseCase: ToggleFavoriteTvShowUseCase = mockk()
    private val getContinueWatchingUseCase: GetContinueWatchingUseCase = mockk()
    private val getPublicDomainTvEpisodesUseCase: GetPublicDomainTvEpisodesUseCase = mockk()

    private val getFreeIptvChannelsUseCase: GetFreeIptvChannelsUseCase = mockk()
    private val torrentRepository: com.moviestreamer.data.repository.TorrentRepository = mockk()

    private val sampleMovies = listOf(
        Movie(id = 1, title = "Movie 1", overview = "Desc", posterPath = null,
            backdropPath = null, releaseDate = "2020-01-01", voteAverage = 7.5, voteCount = 100)
    )
    private val sampleTvShows = listOf(
        TvShow(id = 10, name = "Show 1", overview = "Desc", posterPath = null,
            backdropPath = null, firstAirDate = "2021-01-01", voteAverage = 8.0, voteCount = 300)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Default: all flows return empty
        every { getFavoriteMoviesUseCase() } returns flowOf(emptyList())
        every { getFavoriteTvShowsUseCase() } returns flowOf(emptyList())
        every { getContinueWatchingUseCase() } returns flowOf(emptyList())

        // Default: all suspend use cases return success with empty lists
        coEvery { getPopularMoviesUseCase() } returns Result.success(emptyList())
        coEvery { getTopRatedMoviesUseCase() } returns Result.success(emptyList())
        coEvery { getPublicDomainMoviesUseCase() } returns emptyList()
        coEvery { getPopularTvShowsUseCase() } returns Result.success(emptyList())
        coEvery { getTopRatedTvShowsUseCase() } returns Result.success(emptyList())
        coEvery { getAiringTodayTvShowsUseCase() } returns Result.success(emptyList())
        coEvery { getPublicDomainTvEpisodesUseCase() } returns emptyList()
        coEvery { getFreeIptvChannelsUseCase() } returns emptyList()
        // Local filter use case — return empty list for any query
        every { searchMoviesUseCase(any(), any()) } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        getPopularMoviesUseCase = getPopularMoviesUseCase,
        getTopRatedMoviesUseCase = getTopRatedMoviesUseCase,
        getPublicDomainMoviesUseCase = getPublicDomainMoviesUseCase,
        searchMoviesUseCase = searchMoviesUseCase,
        getPopularTvShowsUseCase = getPopularTvShowsUseCase,
        getTopRatedTvShowsUseCase = getTopRatedTvShowsUseCase,
        getAiringTodayTvShowsUseCase = getAiringTodayTvShowsUseCase,
        getTvShowDetailsUseCase = getTvShowDetailsUseCase,
        getTvSeasonDetailsUseCase = getTvSeasonDetailsUseCase,
        getFavoriteMoviesUseCase = getFavoriteMoviesUseCase,
        getFavoriteTvShowsUseCase = getFavoriteTvShowsUseCase,
        toggleFavoriteMovieUseCase = toggleFavoriteMovieUseCase,
        toggleFavoriteTvShowUseCase = toggleFavoriteTvShowUseCase,
        getContinueWatchingUseCase = getContinueWatchingUseCase,
        getPublicDomainTvEpisodesUseCase = getPublicDomainTvEpisodesUseCase,
        getFreeIptvChannelsUseCase = getFreeIptvChannelsUseCase,
        torrentRepository = torrentRepository
    )

    @Test
    fun `initial state is loading`() = runTest {
        val viewModel = createViewModel()
        // After init, isLoading should be false (content loaded synchronously via UnconfinedTestDispatcher)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadContent populates popular movies`() = runTest {
        coEvery { getPopularMoviesUseCase() } returns Result.success(sampleMovies)

        val viewModel = createViewModel()

        assertEquals(sampleMovies, viewModel.uiState.value.popularMovies)
    }

    @Test
    fun `loadContent populates TV shows`() = runTest {
        coEvery { getPopularTvShowsUseCase() } returns Result.success(sampleTvShows)

        val viewModel = createViewModel()

        assertEquals(sampleTvShows, viewModel.uiState.value.popularTvShows)
    }

    @Test
    fun `loadContent error sets error state`() = runTest {
        coEvery { getPopularMoviesUseCase() } throws RuntimeException("Network error")

        val viewModel = createViewModel()

        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `updateSearchQuery updates state`() = runTest {
        val viewModel = createViewModel()

        viewModel.updateSearchQuery("batman")

        assertEquals("batman", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `retry clears error and reloads content`() = runTest {
        coEvery { getPopularMoviesUseCase() } throws RuntimeException("Network error") andThen Result.success(sampleMovies)

        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.retry()

        assertNull(viewModel.uiState.value.error)
        assertEquals(sampleMovies, viewModel.uiState.value.popularMovies)
    }

    @Test
    fun `favorite movies flow updates state`() = runTest {
        every { getFavoriteMoviesUseCase() } returns flowOf(sampleMovies)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(sampleMovies, state.favoriteMovies)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `continue watching flow updates state`() = runTest {
        val cwItems = listOf(
            ContinueWatchingEntity(
                contentId = "1", title = "Movie 1", posterPath = null,
                videoUrl = "http://example.com/movie.mp4", progressMs = 30000L, durationMs = 120000L
            )
        )
        every { getContinueWatchingUseCase() } returns flowOf(cwItems)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(cwItems, state.continueWatching)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
