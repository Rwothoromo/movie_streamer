package com.moviestreamer.domain.usecase

import com.moviestreamer.data.TvShow
import com.moviestreamer.data.repository.MovieRepository

class GetTvShowsByGenreUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(genreId: Int): Result<List<TvShow>> =
        repository.getTvShowsByGenre(genreId)
}
