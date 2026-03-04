package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Season
import com.moviestreamer.data.repository.MovieRepository

class GetTvSeasonDetailsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(tvId: Int, seasonNumber: Int): Result<Season> =
        repository.getTvSeasonDetails(tvId, seasonNumber)
}
