package com.moviestreamer.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.moviestreamer.data.PublicDomainMovies
import com.moviestreamer.data.local.AppDatabase
import com.moviestreamer.data.local.AppPreferencesManager
import java.util.concurrent.TimeUnit

class EngagementNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val preferencesManager = AppPreferencesManager(applicationContext)
        val state = preferencesManager.state.value
        if (!state.notificationsEnabled) {
            return Result.success()
        }

        val effectiveProfileId = state.activeProfileId.takeIf { it > 0 }
            ?: state.defaultProfileId.takeIf { it > 0 }
            ?: 1L

        val database = AppDatabase.create(applicationContext)
        val continueWatching = database.continueWatchingDao().getContinueWatchingSnapshot(effectiveProfileId)

        if (state.reminderNotificationsEnabled && continueWatching.isNotEmpty()) {
            val now = System.currentTimeMillis()
            val twelveHoursMs = TimeUnit.HOURS.toMillis(12)
            if (now - preferencesManager.lastReminderTimestamp >= twelveHoursMs) {
                val item = continueWatching.first()
                NotificationHelper.notifyReminder(
                    applicationContext,
                    title = "Pick up where you left off",
                    message = "Resume ${item.title} from your ${((item.progressMs.toFloat() / item.durationMs.coerceAtLeast(1)) * 100).toInt()}% mark."
                )
                preferencesManager.lastReminderTimestamp = now
            }
        }

        val catalogSize = PublicDomainMovies.publicDomainMovies.size + PublicDomainMovies.publicDomainTvEpisodes.size
        if (state.newContentNotificationsEnabled) {
            val previousSize = preferencesManager.lastKnownCatalogSize
            if (previousSize > 0 && catalogSize > previousSize) {
                NotificationHelper.notifyNewContent(
                    applicationContext,
                    title = "Fresh free titles available",
                    message = "Browse the latest additions in the Movie Streamer catalog."
                )
            }
            preferencesManager.lastKnownCatalogSize = catalogSize
        }

        return Result.success()
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "engagement_notifications"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<EngagementNotificationWorker>(12, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
