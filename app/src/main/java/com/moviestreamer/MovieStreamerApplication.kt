package com.moviestreamer

import android.app.Application
import com.moviestreamer.di.databaseModule
import com.moviestreamer.di.networkModule
import com.moviestreamer.di.repositoryModule
import com.moviestreamer.di.useCaseModule
import com.moviestreamer.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MovieStreamerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MovieStreamerApplication)
            modules(networkModule, databaseModule, repositoryModule, useCaseModule, viewModelModule)
        }
    }
}
