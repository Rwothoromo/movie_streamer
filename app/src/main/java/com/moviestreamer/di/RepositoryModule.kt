package com.moviestreamer.di

import com.moviestreamer.data.repository.LocalRepository
import com.moviestreamer.data.repository.LocalRepositoryImpl
import com.moviestreamer.data.repository.MovieRepository
import com.moviestreamer.data.repository.MovieRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single<LocalRepository> {
        LocalRepositoryImpl(
            get(), // FavoriteMovieDao
            get(), // FavoriteTvShowDao
            get()  // ContinueWatchingDao
        )
    }
    single { com.moviestreamer.download.MovieDownloadManager(get()) }
}
