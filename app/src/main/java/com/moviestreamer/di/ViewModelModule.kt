package com.moviestreamer.di

import com.moviestreamer.ui.GenreViewModel
import com.moviestreamer.ui.HomeViewModel
import com.moviestreamer.ui.SearchViewModel
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
            getTvSeasonDetailsUseCase = get(),
            getFavoriteMoviesUseCase = get(),
            getFavoriteTvShowsUseCase = get(),
            toggleFavoriteMovieUseCase = get(),
            toggleFavoriteTvShowUseCase = get(),
            getContinueWatchingUseCase = get(),
            getPublicDomainTvEpisodesUseCase = get(),
            getFreeIptvChannelsUseCase = get(),
            localRepository = get(),
            preferencesManager = get(),
            torrentRepository = get()
        )
    }
    viewModel { SearchViewModel(searchMoviesApiUseCase = get(), searchTvShowsUseCase = get()) }
    viewModel { GenreViewModel(getMoviesByGenreUseCase = get(), getTvShowsByGenreUseCase = get()) }
}
