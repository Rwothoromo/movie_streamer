package com.moviestreamer.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.moviestreamer.R

object NotificationHelper {
    const val CHANNEL_NEW_CONTENT = "new_content"
    const val CHANNEL_REMINDERS = "continue_watching_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(
                CHANNEL_NEW_CONTENT,
                "New Content",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when new public-domain titles are available"
            },
            NotificationChannel(
                CHANNEL_REMINDERS,
                "Continue Watching Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle reminders to finish unfinished content"
            }
        )
        notificationManager.createNotificationChannels(channels)
    }

    fun notifyNewContent(context: Context, title: String, message: String) {
        postNotification(context, CHANNEL_NEW_CONTENT, 1001, title, message)
    }

    fun notifyReminder(context: Context, title: String, message: String) {
        postNotification(context, CHANNEL_REMINDERS, 1002, title, message)
    }

    private fun postNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        message: String
    ) {
        val launchIntent = context.packageManager.getLeanbackLaunchIntentForPackage(context.packageName)
            ?: context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: Intent()
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        runCatching {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}
