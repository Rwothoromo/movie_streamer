package com.moviestreamer.tv.repository

import com.moviestreamer.tv.model.Movie
import com.moviestreamer.tv.network.RetrofitClient

class MovieRepository {
    // In a real app, store this securely or use BuildConfig
    // Get your API key from https://www.themoviedb.org/settings/api
    private val apiKey = "YOUR_TMDB_API_KEY_HERE"
    
    private val apiService = RetrofitClient.tmdbApiService
    
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> {
        return try {
            val response = apiService.getPopularMovies(apiKey, page)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>> {
        return try {
            val response = apiService.getTopRatedMovies(apiKey, page)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUpcomingMovies(page: Int = 1): Result<List<Movie>> {
        return try {
            val response = apiService.getUpcomingMovies(apiKey, page)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
