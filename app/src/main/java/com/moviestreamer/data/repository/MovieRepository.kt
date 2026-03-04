package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie

interface MovieRepository {
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>>
    fun getPublicDomainMovies(): List<Movie>
}
