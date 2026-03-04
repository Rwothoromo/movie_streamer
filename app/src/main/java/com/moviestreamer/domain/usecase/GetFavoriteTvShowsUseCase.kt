package com.moviestreamer.domain.usecase

import com.moviestreamer.data.TvShow
import com.moviestreamer.data.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteTvShowsUseCase(private val repository: LocalRepository) {
    operator fun invoke(): Flow<List<TvShow>> = repository.getFavoriteTvShows()
}
