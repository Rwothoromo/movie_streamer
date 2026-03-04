package com.moviestreamer.domain.usecase

import com.moviestreamer.data.TvShow
import com.moviestreamer.data.repository.MovieRepository

class SearchTvShowsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String): Result<List<TvShow>> =
        repository.searchTvShows(query)
}
