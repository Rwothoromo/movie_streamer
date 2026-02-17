package com.moviestreamer.tv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.tv.model.Movie
import com.moviestreamer.tv.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val movies: List<Movie>) : UiState()
    data class Error(val message: String) : UiState()
}

class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        loadPopularMovies()
    }
    
    fun loadPopularMovies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getPopularMovies().fold(
                onSuccess = { movies ->
                    _uiState.value = UiState.Success(movies)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    fun loadTopRatedMovies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getTopRatedMovies().fold(
                onSuccess = { movies ->
                    _uiState.value = UiState.Success(movies)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}
