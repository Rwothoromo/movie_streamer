package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.MovieRepository

class SearchMoviesApiUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String): Result<List<Movie>> =
        repository.searchMovies(query)
}
