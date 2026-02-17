package com.moviestreamer.tv.repository

import com.moviestreamer.tv.model.Movie
import com.moviestreamer.tv.network.RetrofitClient

class MovieRepository {
    // SECURITY WARNING: API key is hardcoded for simplicity in this demo
    // In production, use one of these secure methods:
    // 1. BuildConfig: Store in gradle.properties (not in git) and access via BuildConfig.TMDB_API_KEY
    // 2. Android Keystore: For highly sensitive keys
    // 3. Environment variables: For CI/CD pipelines
    // 4. Remote config: Fetch from secure backend
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
