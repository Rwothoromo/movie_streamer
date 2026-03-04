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
    suspend fun searchMovies(query: String): Result<List<Movie>>
    suspend fun searchTvShows(query: String): Result<List<TvShow>>
    suspend fun getMoviesByGenre(genreId: Int): Result<List<Movie>>
    suspend fun getTvShowsByGenre(genreId: Int): Result<List<TvShow>>
    fun getPublicDomainTvEpisodes(): List<Movie>
    fun getFreeIptvChannels(): List<Movie>
    suspend fun getMovieTrailerKey(movieId: Int): Result<String?>
}
