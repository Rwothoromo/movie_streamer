package com.moviestreamer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.data.Genre
import com.moviestreamer.data.Movie
import com.moviestreamer.data.MovieGenres
import com.moviestreamer.data.TvGenres
import com.moviestreamer.data.TvShow
import com.moviestreamer.domain.usecase.GetMoviesByGenreUseCase
import com.moviestreamer.domain.usecase.GetTvShowsByGenreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ContentTab { MOVIES, TV_SHOWS }

data class GenreUiState(
    val selectedTab: ContentTab = ContentTab.MOVIES,
    val selectedGenre: Genre? = null,
    val movieGenres: List<Genre> = MovieGenres.all,
    val tvGenres: List<Genre> = TvGenres.all,
    val movieResults: List<Movie> = emptyList(),
    val tvShowResults: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class GenreViewModel(
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase,
    private val getTvShowsByGenreUseCase: GetTvShowsByGenreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenreUiState())
    val uiState: StateFlow<GenreUiState> = _uiState.asStateFlow()

    fun selectTab(tab: ContentTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            selectedGenre = null,
            movieResults = emptyList(),
            tvShowResults = emptyList()
        )
    }

    fun selectGenre(genre: Genre) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre, isLoading = true, error = null)
        viewModelScope.launch {
            when (_uiState.value.selectedTab) {
                ContentTab.MOVIES -> {
                    val result = getMoviesByGenreUseCase(genre.id)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        movieResults = result.getOrDefault(emptyList()),
                        error = result.exceptionOrNull()?.message
                    )
                }
                ContentTab.TV_SHOWS -> {
                    val result = getTvShowsByGenreUseCase(genre.id)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tvShowResults = result.getOrDefault(emptyList()),
                        error = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}
