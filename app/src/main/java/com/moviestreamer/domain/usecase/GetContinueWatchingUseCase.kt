package com.moviestreamer.domain.usecase

import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class GetContinueWatchingUseCase(private val repository: LocalRepository) {
    operator fun invoke(): Flow<List<ContinueWatchingEntity>> = repository.getContinueWatching()
}
