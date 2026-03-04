package com.moviestreamer.domain.usecase

import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.repository.LocalRepository

class SaveContinueWatchingUseCase(private val repository: LocalRepository) {
    suspend operator fun invoke(item: ContinueWatchingEntity) =
        repository.upsertContinueWatching(item)
}
