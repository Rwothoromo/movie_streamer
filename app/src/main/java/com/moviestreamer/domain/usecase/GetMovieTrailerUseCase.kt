package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Video
import com.moviestreamer.data.repository.MovieRepository

/** Fetches YouTube trailer IDs for a movie from TMDB. Returns the first YouTube trailer key. */
class GetMovieTrailerUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): Result<String?> =
        repository.getMovieTrailerKey(movieId)
}
