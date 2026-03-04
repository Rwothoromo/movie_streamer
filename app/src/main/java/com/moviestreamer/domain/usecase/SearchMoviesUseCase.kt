package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie

class SearchMoviesUseCase {
    operator fun invoke(query: String, movies: List<Movie>): List<Movie> {
        if (query.isBlank()) return emptyList()
        return movies.filter { movie ->
            movie.title.contains(query, ignoreCase = true) ||
            movie.overview?.contains(query, ignoreCase = true) == true
        }
    }
}
