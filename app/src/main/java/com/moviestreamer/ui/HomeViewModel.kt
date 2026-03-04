package com.moviestreamer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.data.Movie
import com.moviestreamer.domain.usecase.GetPopularMoviesUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedMoviesUseCase
import com.moviestreamer.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val publicDomainMovies: List<Movie> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMovies: GetPopularMoviesUseCase,
    private val getTopRatedMovies: GetTopRatedMoviesUseCase,
    private val getPublicDomainMovies: GetPublicDomainMoviesUseCase,
    private val searchMovies: SearchMoviesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allMovies: List<Movie> = emptyList()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val publicDomain = getPublicDomainMovies()

                var popular = emptyList<Movie>()
                var topRated = emptyList<Movie>()

                try {
                    popular = getPopularMovies().getOrElse { e ->
                        Log.e("HomeViewModel", "Failed to load popular movies", e)
                        emptyList()
                    }
                    topRated = getTopRatedMovies().getOrElse { e ->
                        Log.e("HomeViewModel", "Failed to load top rated movies", e)
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Failed to load TMDB data", e)
                }

                _uiState.value = HomeUiState(
                    popularMovies = popular,
                    topRatedMovies = topRated,
                    publicDomainMovies = publicDomain,
                    isLoading = false
                )

                allMovies = popular + topRated + publicDomain
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

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _uiState.value = _uiState.value.copy(searchResults = searchMovies(query, allMovies))
    }
}
