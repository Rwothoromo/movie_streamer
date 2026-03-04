package com.moviestreamer.domain.usecase

import com.moviestreamer.data.Movie
import com.moviestreamer.data.repository.MovieRepository

class GetPublicDomainTvEpisodesUseCase(private val repository: MovieRepository) {
    operator fun invoke(): List<Movie> = repository.getPublicDomainTvEpisodes()
}
