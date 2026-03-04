package com.moviestreamer.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    // ─── Movies ───────────────────────────────────────────────────────────────

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MoviesResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): MoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): Movie

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Int): CreditsResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") movieId: Int): VideosResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1
    ): MoviesResponse

    // ─── TV Shows ─────────────────────────────────────────────────────────────

    @GET("tv/popular")
    suspend fun getTvPopular(@Query("page") page: Int = 1): TvShowsResponse

    @GET("tv/top_rated")
    suspend fun getTvTopRated(@Query("page") page: Int = 1): TvShowsResponse

    @GET("tv/airing_today")
    suspend fun getTvAiringToday(@Query("page") page: Int = 1): TvShowsResponse

    @GET("tv/on_the_air")
    suspend fun getTvOnTheAir(@Query("page") page: Int = 1): TvShowsResponse

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(@Path("tv_id") tvId: Int): TvShow

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int
    ): Season

    @GET("tv/{tv_id}/season/{season_number}/episode/{episode_number}")
    suspend fun getEpisodeDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int
    ): Episode

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(@Path("tv_id") tvId: Int): CreditsResponse

    @GET("tv/{tv_id}/videos")
    suspend fun getTvVideos(@Path("tv_id") tvId: Int): VideosResponse

    @GET("tv/{tv_id}/similar")
    suspend fun getSimilarTvShows(
        @Path("tv_id") tvId: Int,
        @Query("page") page: Int = 1
    ): TvShowsResponse

    // ─── Search ───────────────────────────────────────────────────────────────

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MoviesResponse

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TvShowsResponse
}
