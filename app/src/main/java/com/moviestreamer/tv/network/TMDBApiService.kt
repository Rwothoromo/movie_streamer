package com.moviestreamer.tv.network

import com.moviestreamer.tv.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * TMDB API Service interface
 * Note: Get your API key from https://www.themoviedb.org/settings/api
 */
interface TMDBApiService {
    
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse
    
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse
    
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieResponse
}
