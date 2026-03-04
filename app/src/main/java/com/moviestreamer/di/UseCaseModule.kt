package com.moviestreamer.di

import com.moviestreamer.domain.usecase.GetAiringTodayTvShowsUseCase
import com.moviestreamer.domain.usecase.GetPopularMoviesUseCase
import com.moviestreamer.domain.usecase.GetPopularTvShowsUseCase
import com.moviestreamer.domain.usecase.GetPublicDomainMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedMoviesUseCase
import com.moviestreamer.domain.usecase.GetTopRatedTvShowsUseCase
import com.moviestreamer.domain.usecase.GetTvSeasonDetailsUseCase
import com.moviestreamer.domain.usecase.GetTvShowDetailsUseCase
import com.moviestreamer.domain.usecase.SearchMoviesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetPopularMoviesUseCase(get()) }
    factory { GetTopRatedMoviesUseCase(get()) }
    factory { GetPublicDomainMoviesUseCase(get()) }
    factory { SearchMoviesUseCase() }
    factory { GetPopularTvShowsUseCase(get()) }
    factory { GetTopRatedTvShowsUseCase(get()) }
    factory { GetAiringTodayTvShowsUseCase(get()) }
    factory { GetTvShowDetailsUseCase(get()) }
    factory { GetTvSeasonDetailsUseCase(get()) }
}
