package com.moviestreamer.di

import com.moviestreamer.ui.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            getPopularMoviesUseCase = get(),
            getTopRatedMoviesUseCase = get(),
            getPublicDomainMoviesUseCase = get(),
            searchMoviesUseCase = get(),
            getPopularTvShowsUseCase = get(),
            getTopRatedTvShowsUseCase = get(),
            getAiringTodayTvShowsUseCase = get(),
            getTvShowDetailsUseCase = get(),
            getTvSeasonDetailsUseCase = get()
        )
    }
}
