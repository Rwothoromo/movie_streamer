package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.PublicDomainMovies
import com.moviestreamer.data.TmdbApi
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbApi: TmdbApi
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> = runCatching {
        tmdbApi.getPopularMovies(page = page).results
    }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> = runCatching {
        tmdbApi.getTopRatedMovies(page = page).results
    }

    override fun getPublicDomainMovies(): List<Movie> = PublicDomainMovies.publicDomainMovies
}
