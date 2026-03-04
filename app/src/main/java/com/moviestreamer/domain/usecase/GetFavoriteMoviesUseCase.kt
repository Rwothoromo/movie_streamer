package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteMoviesUseCase(private val repository: LocalRepository) {
    operator fun invoke(): Flow<List<Movie>> = repository.getFavoriteMovies()
}
