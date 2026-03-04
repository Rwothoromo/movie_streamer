package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.PublicDomainMovies
import com.moviestreamer.data.Season
import com.moviestreamer.data.TmdbApi
import com.moviestreamer.data.TvShow

class MovieRepositoryImpl(private val tmdbApi: TmdbApi) : MovieRepository {
    override suspend fun getPopularMovies(page: Int): Result<List<Movie>> = runCatching {
        tmdbApi.getPopularMovies(page).results
    }

    override suspend fun getTopRatedMovies(page: Int): Result<List<Movie>> = runCatching {
        tmdbApi.getTopRatedMovies(page).results
    }

    override fun getPublicDomainMovies(): List<Movie> = PublicDomainMovies.publicDomainMovies

    override suspend fun getPopularTvShows(page: Int): Result<List<TvShow>> = runCatching {
        tmdbApi.getTvPopular(page).results
    }

    override suspend fun getTopRatedTvShows(page: Int): Result<List<TvShow>> = runCatching {
        tmdbApi.getTvTopRated(page).results
    }

    override suspend fun getAiringTodayTvShows(page: Int): Result<List<TvShow>> = runCatching {
        tmdbApi.getTvAiringToday(page).results
    }

    override suspend fun getTvShowDetails(tvId: Int): Result<TvShow> = runCatching {
        tmdbApi.getTvShowDetails(tvId)
    }

    override suspend fun getTvSeasonDetails(tvId: Int, seasonNumber: Int): Result<Season> = runCatching {
        tmdbApi.getSeasonDetails(tvId, seasonNumber)
    }
}
