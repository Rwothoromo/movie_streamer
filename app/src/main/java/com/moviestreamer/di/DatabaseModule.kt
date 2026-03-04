package com.moviestreamer.di

import com.moviestreamer.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().favoriteMovieDao() }
    single { get<AppDatabase>().favoriteTvShowDao() }
    single { get<AppDatabase>().continueWatchingDao() }
}
