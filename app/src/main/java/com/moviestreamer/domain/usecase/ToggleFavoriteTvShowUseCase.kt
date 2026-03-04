package com.moviestreamer.domain.usecase

import com.moviestreamer.data.TvShow
import com.moviestreamer.data.repository.LocalRepository

class ToggleFavoriteTvShowUseCase(private val repository: LocalRepository) {
    suspend operator fun invoke(tvShow: TvShow): Boolean {
        return if (repository.isFavoriteTvShow(tvShow.id)) {
            repository.removeFavoriteTvShow(tvShow.id)
            false
        } else {
            repository.addFavoriteTvShow(tvShow)
            true
        }
    }
}
