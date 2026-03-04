package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.Season
import com.moviestreamer.data.TvShow

interface MovieRepository {
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>>
    fun getPublicDomainMovies(): List<Movie>
    suspend fun getPopularTvShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getTopRatedTvShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getAiringTodayTvShows(page: Int = 1): Result<List<TvShow>>
    suspend fun getTvShowDetails(tvId: Int): Result<TvShow>
    suspend fun getTvSeasonDetails(tvId: Int, seasonNumber: Int): Result<Season>
}
