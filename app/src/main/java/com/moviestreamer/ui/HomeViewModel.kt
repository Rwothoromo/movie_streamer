package com.moviestreamer.ui

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
                
                if (BuildConfig.TMDB_API_KEY != "YOUR_TMDB_API_KEY_HERE") {
                    try {
                        popular = ApiClient.tmdbApi.getPopularMovies(
                            apiKey = BuildConfig.TMDB_API_KEY,
                            page = 1
                        ).results
                        
                        topRated = ApiClient.tmdbApi.getTopRatedMovies(
                            apiKey = BuildConfig.TMDB_API_KEY,
                            page = 1
                        ).results
                    } catch (e: Exception) {
                        // If TMDB fails, we still have public domain content
                        e.printStackTrace()
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
