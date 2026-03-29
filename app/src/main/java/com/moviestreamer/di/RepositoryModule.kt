package com.moviestreamer.di

import com.moviestreamer.data.repository.LocalRepository
import com.moviestreamer.data.repository.LocalRepositoryImpl
import com.moviestreamer.data.repository.MovieRepository
import com.moviestreamer.data.repository.MovieRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get()) }
    single<com.moviestreamer.data.repository.TorrentRepository> { com.moviestreamer.data.repository.TorrentRepositoryImpl(get()) }
    single<LocalRepository> {
        LocalRepositoryImpl(
            get(), // FavoriteMovieDao
            get(), // FavoriteTvShowDao
            get()  // ContinueWatchingDao
        )
    }
    single { com.moviestreamer.download.MovieDownloadManager(get()) }
}
