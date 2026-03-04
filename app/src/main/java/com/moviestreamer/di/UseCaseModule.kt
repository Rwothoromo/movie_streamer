package com.moviestreamer.di

import com.moviestreamer.domain.usecase.GetAiringTodayTvShowsUseCase
import com.moviestreamer.domain.usecase.GetContinueWatchingUseCase
import com.moviestreamer.domain.usecase.GetFavoriteMoviesUseCase
import com.moviestreamer.domain.usecase.GetFavoriteTvShowsUseCase
import com.moviestreamer.domain.usecase.GetMoviesByGenreUseCase
import com.moviestreamer.domain.usecase.GetPopularMoviesUseCase
import com.moviestreamer.domain.usecase.GetPopularTvShowsUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedTvShowsUseCase
import com.moviestreamer.domain.usecase.GetTvSeasonDetailsUseCase
import com.moviestreamer.domain.usecase.GetTvShowDetailsUseCase
import com.moviestreamer.domain.usecase.GetTvShowsByGenreUseCase
import com.moviestreamer.domain.usecase.SaveContinueWatchingUseCase
import com.moviestreamer.domain.usecase.SearchMoviesApiUseCase
import com.moviestreamer.domain.usecase.SearchMoviesUseCase
import com.moviestreamer.domain.usecase.SearchTvShowsUseCase
import com.moviestreamer.domain.usecase.ToggleFavoriteMovieUseCase
import com.moviestreamer.domain.usecase.ToggleFavoriteTvShowUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainTvEpisodesUseCase
import com.moviestreamer.domain.usecase.GetFreeIptvChannelsUseCase
import com.moviestreamer.domain.usecase.GetMovieTrailerUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetPopularMoviesUseCase(get()) }
    factory { GetTopRatedMoviesUseCase(get()) }
    factory { GetPublicDomainMoviesUseCase(get()) }
    factory { SearchMoviesUseCase() }
    factory { SearchMoviesApiUseCase(get()) }
    factory { SearchTvShowsUseCase(get()) }
    factory { GetPopularTvShowsUseCase(get()) }
    factory { GetTopRatedTvShowsUseCase(get()) }
    factory { GetAiringTodayTvShowsUseCase(get()) }
    factory { GetTvShowDetailsUseCase(get()) }
    factory { GetTvSeasonDetailsUseCase(get()) }
    factory { GetFavoriteMoviesUseCase(get()) }
    factory { ToggleFavoriteMovieUseCase(get()) }
    factory { GetFavoriteTvShowsUseCase(get()) }
    factory { ToggleFavoriteTvShowUseCase(get()) }
    factory { GetContinueWatchingUseCase(get()) }
    factory { SaveContinueWatchingUseCase(get()) }
    factory { GetMoviesByGenreUseCase(get()) }
    factory { GetTvShowsByGenreUseCase(get()) }
    factory { GetPublicDomainTvEpisodesUseCase(get()) }
    factory { GetFreeIptvChannelsUseCase(get()) }
    factory { GetMovieTrailerUseCase(get()) }
}
