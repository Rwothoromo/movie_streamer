package com.moviestreamer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.BuildConfig
import com.moviestreamer.data.ApiClient
import com.moviestreamer.data.Movie
import com.moviestreamer.data.PublicDomainMovies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val publicDomainMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadMovies()
    }
    
    private fun loadMovies() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Load public domain movies (always available)
                val publicDomain = PublicDomainMovies.getPublicDomainMovies()
                
                // Try to load TMDB data if API key is configured
                var popular = emptyList<Movie>()
                var topRated = emptyList<Movie>()
                
                val apiKey = BuildConfig.TMDB_API_KEY
                // Check if API key is valid (not a placeholder pattern)
                val placeholderPatterns = listOf("YOUR_", "REPLACE", "PLACEHOLDER", "EXAMPLE")
                val isValidKey = apiKey.isNotBlank() && 
                    placeholderPatterns.none { apiKey.trim().uppercase().startsWith(it) }
                
                if (isValidKey) {
                    try {
                        popular = ApiClient.tmdbApi.getPopularMovies(
                            apiKey = apiKey,
                            page = 1
                        ).results
                        
                        topRated = ApiClient.tmdbApi.getTopRatedMovies(
                            apiKey = apiKey,
                            page = 1
                        ).results
                    } catch (e: Exception) {
                        // If TMDB fails, we still have public domain content
                        Log.e("HomeViewModel", "Failed to load TMDB data", e)
                    }
                }
                
                _uiState.value = HomeUiState(
                    popularMovies = popular,
                    topRatedMovies = topRated,
                    publicDomainMovies = publicDomain,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun retry() {
        loadMovies()
    }
}
