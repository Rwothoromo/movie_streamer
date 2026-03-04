package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.LocalRepository

class ToggleFavoriteMovieUseCase(private val repository: LocalRepository) {
    suspend operator fun invoke(movie: Movie): Boolean {
        return if (repository.isFavoriteMovie(movie.id)) {
            repository.removeFavoriteMovie(movie.id)
            false
        } else {
            repository.addFavoriteMovie(movie)
            true
        }
    }
}
