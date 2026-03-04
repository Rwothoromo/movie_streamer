package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.MovieRepository

class GetMoviesByGenreUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(genreId: Int): Result<List<Movie>> =
        repository.getMoviesByGenre(genreId)
}
