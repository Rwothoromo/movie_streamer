package com.moviestreamer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.domain.usecase.SearchMoviesApiUseCase
import com.moviestreamer.domain.usecase.SearchTvShowsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val movieResults: List<Movie> = emptyList(),
    val tvShowResults: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SearchViewModel(
    private val searchMoviesApiUseCase: SearchMoviesApiUseCase,
    private val searchTvShowsUseCase: SearchTvShowsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                movieResults = emptyList(),
                tvShowResults = emptyList(),
                isLoading = false,
                error = null
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            val activeQuery = query.trim()
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val movieDeferred = async(Dispatchers.IO) { searchMoviesApiUseCase(activeQuery) }
            val tvDeferred = async(Dispatchers.IO) { searchTvShowsUseCase(activeQuery) }

            val movieResult = movieDeferred.await()
            val tvResult = tvDeferred.await()

            if (_uiState.value.query != query) return@launch

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                movieResults = movieResult.getOrDefault(emptyList()),
                tvShowResults = tvResult.getOrDefault(emptyList()),
                error = movieResult.exceptionOrNull()?.message ?: tvResult.exceptionOrNull()?.message
            )
        }
    }
}
