package com.moviestreamer.domain.usecase

import com.moviestreamer.data.TvShow
import com.moviestreamer.data.repository.MovieRepository

class GetTvShowDetailsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(tvId: Int): Result<TvShow> =
        repository.getTvShowDetails(tvId)
}
