package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.MovieRepository

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(page: Int = 1): Result<List<Movie>> =
        repository.getPopularMovies(page)
}
