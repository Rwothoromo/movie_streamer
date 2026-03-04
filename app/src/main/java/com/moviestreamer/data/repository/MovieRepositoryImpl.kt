package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.PublicDomainMovies
import com.moviestreamer.data.TmdbApi

class MovieRepositoryImpl(private val tmdbApi: TmdbApi) : MovieRepository {
    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> = runCatching {
        tmdbApi.getPopularMovies(page).results
    }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> = runCatching {
        tmdbApi.getTopRatedMovies(page).results
    }

    override fun getPublicDomainMovies(): List<Movie> = PublicDomainMovies.publicDomainMovies
}
