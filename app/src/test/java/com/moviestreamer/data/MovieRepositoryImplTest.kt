package com.moviestreamer.data

import com.moviestreamer.data.repository.MovieRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MovieRepositoryImplTest {

    private val tmdbApi: TmdbApi = mockk()
    private lateinit var repository: MovieRepositoryImpl

    private val sampleMoviesResponse = MoviesResponse(
        results = listOf(
            Movie(id = 1, title = "Test Movie", overview = "Overview",
                posterPath = "/poster.jpg", backdropPath = null,
                releaseDate = "2023-01-01", voteAverage = 7.5, voteCount = 200)
        ),
        page = 1,
        totalPages = 10,
        totalResults = 100
    )
    private val sampleTvResponse = TvShowsResponse(
        results = listOf(
            TvShow(id = 5, name = "Test Show", overview = "Overview",
                posterPath = "/poster.jpg", backdropPath = null,
                firstAirDate = "2022-01-01", voteAverage = 8.0, voteCount = 500)
        ),
        page = 1,
        totalPages = 5,
        totalResults = 50
    )

    @Before
    fun setUp() {
        repository = MovieRepositoryImpl(tmdbApi)
    }

    @Test
    fun `getPopularMovies returns success with movie list`() = runTest {
        coEvery { tmdbApi.getPopularMovies(1) } returns sampleMoviesResponse

        val result = repository.getPopularMovies(1)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Movie", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `getPopularMovies returns failure on network error`() = runTest {
        coEvery { tmdbApi.getPopularMovies(1) } throws RuntimeException("No internet")

        val result = repository.getPopularMovies(1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getTopRatedMovies returns success`() = runTest {
        coEvery { tmdbApi.getTopRatedMovies(1) } returns sampleMoviesResponse

        val result = repository.getTopRatedMovies(1)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `searchMovies returns matching results`() = runTest {
        coEvery { tmdbApi.searchMovies("batman") } returns sampleMoviesResponse

        val result = repository.searchMovies("batman")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `getPopularTvShows returns success`() = runTest {
        coEvery { tmdbApi.getTvPopular(1) } returns sampleTvResponse

        val result = repository.getPopularTvShows(1)

        assertTrue(result.isSuccess)
        assertEquals("Test Show", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `getPublicDomainMovies returns non-empty list`() {
        val movies = repository.getPublicDomainMovies()

        assertFalse(movies.isEmpty())
    }

    @Test
    fun `getPublicDomainTvEpisodes returns list`() {
        val episodes = repository.getPublicDomainTvEpisodes()

        assertNotNull(episodes)
    }

    @Test
    fun `getFreeIptvChannels returns list`() {
        val channels = repository.getFreeIptvChannels()

        assertNotNull(channels)
    }

    @Test
    fun `getMoviesByGenre returns success`() = runTest {
        coEvery { tmdbApi.discoverMoviesByGenre(28) } returns sampleMoviesResponse

        val result = repository.getMoviesByGenre(28)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `getTvShowsByGenre returns success`() = runTest {
        coEvery { tmdbApi.discoverTvShowsByGenre(18) } returns sampleTvResponse

        val result = repository.getTvShowsByGenre(18)

        assertTrue(result.isSuccess)
    }
}
