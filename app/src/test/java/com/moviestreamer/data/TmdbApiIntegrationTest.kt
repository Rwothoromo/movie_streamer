package com.moviestreamer.data

import com.moviestreamer.data.repository.MovieRepositoryImpl
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TmdbApiIntegrationTest {

    private lateinit var server: MockWebServer
    private lateinit var api: TmdbApi
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)

        repository = MovieRepositoryImpl(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getPopularMovies parses JSON response correctly`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "page": 1,
                      "results": [
                        {
                          "id": 123,
                          "title": "Inception",
                          "overview": "A thief who steals corporate secrets.",
                          "poster_path": "/poster.jpg",
                          "backdrop_path": null,
                          "release_date": "2010-07-16",
                          "vote_average": 8.8,
                          "vote_count": 35000
                        }
                      ],
                      "total_pages": 500,
                      "total_results": 10000
                    }
                    """.trimIndent()
                )
        )

        val result = repository.getPopularMovies(1)

        assertTrue(result.isSuccess)
        val movies = result.getOrThrow()
        assertEquals(1, movies.size)
        assertEquals(123, movies[0].id)
        assertEquals("Inception", movies[0].title)
        assertEquals(8.8, movies[0].voteAverage!!, 0.01)
    }

    @Test
    fun `searchMovies sends correct query parameter`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"page":1,"results":[],"total_pages":1,"total_results":0}""")
        )

        repository.searchMovies("batman")

        val request = server.takeRequest()
        assertTrue(request.path?.contains("query=batman") == true)
    }

    @Test
    fun `getPopularMovies returns failure on server error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))

        val result = repository.getPopularMovies(1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getPopularMovies returns failure on malformed JSON`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("not json {{{")
        )

        val result = repository.getPopularMovies(1)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getTvPopular parses TV show response`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "page": 1,
                      "results": [
                        {
                          "id": 456,
                          "name": "Breaking Bad",
                          "overview": "Chemistry teacher turns drug dealer.",
                          "poster_path": "/poster.jpg",
                          "backdrop_path": null,
                          "first_air_date": "2008-01-20",
                          "vote_average": 9.5
                        }
                      ],
                      "total_pages": 100,
                      "total_results": 2000
                    }
                    """.trimIndent()
                )
        )

        val result = repository.getPopularTvShows(1)

        assertTrue(result.isSuccess)
        val shows = result.getOrThrow()
        assertEquals(1, shows.size)
        assertEquals("Breaking Bad", shows[0].name)
    }
}
