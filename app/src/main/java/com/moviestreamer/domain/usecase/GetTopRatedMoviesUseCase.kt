package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.MovieRepository

class GetTopRatedMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(page: Int = 1): Result<List<Movie>> =
        repository.getTopRatedMovies(page)
}
