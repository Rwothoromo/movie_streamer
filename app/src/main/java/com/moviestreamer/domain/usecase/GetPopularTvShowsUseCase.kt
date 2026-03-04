package com.moviestreamer.domain.usecase

import com.moviestreamer.data.TvShow
import com.moviestreamer.data.repository.MovieRepository

class GetPopularTvShowsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(page: Int = 1): Result<List<TvShow>> =
        repository.getPopularTvShows(page)
}
