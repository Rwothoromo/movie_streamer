package com.moviestreamer

import android.app.Application
import com.moviestreamer.data.repository.LocalRepository
import com.moviestreamer.di.databaseModule
import com.moviestreamer.di.networkModule
import com.moviestreamer.di.repositoryModule
import com.moviestreamer.di.securityModule
import com.moviestreamer.di.useCaseModule
import com.moviestreamer.di.viewModelModule
import com.moviestreamer.diagnostics.AppDiagnostics
import com.moviestreamer.notifications.EngagementNotificationWorker
import com.moviestreamer.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MovieStreamerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val koinApp = startKoin {
            androidLogger()
            androidContext(this@MovieStreamerApplication)
            modules(networkModule, databaseModule, securityModule, repositoryModule, useCaseModule, viewModelModule)
        }

        NotificationHelper.createNotificationChannels(this)
        EngagementNotificationWorker.schedule(this)
        koinApp.koin.get<AppDiagnostics>().install()
        CoroutineScope(Dispatchers.IO).launch {
            koinApp.koin.get<LocalRepository>().ensureDefaultProfiles()
        }
    }
}
