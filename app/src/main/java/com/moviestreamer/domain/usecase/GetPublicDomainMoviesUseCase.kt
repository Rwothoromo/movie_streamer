package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.MovieRepository
import javax.inject.Inject

class GetPublicDomainMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(): List<Movie> = repository.getPublicDomainMovies()
}
