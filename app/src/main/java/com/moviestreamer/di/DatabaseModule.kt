package com.moviestreamer.di

import com.moviestreamer.data.local.AppDatabase
import com.moviestreamer.data.local.AppPreferencesManager
import com.moviestreamer.diagnostics.AppDiagnostics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.create(androidContext()) }
    single { AppPreferencesManager(androidContext()) }
    single { get<AppDatabase>().favoriteMovieDao() }
    single { get<AppDatabase>().favoriteTvShowDao() }
    single { get<AppDatabase>().continueWatchingDao() }
    single { get<AppDatabase>().userProfileDao() }
    single { get<AppDatabase>().userReviewDao() }
    single { AppDiagnostics(androidContext(), get()) }
}
