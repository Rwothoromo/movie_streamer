package com.moviestreamer.data.local

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppPreferencesState(
    val activeProfileId: Long = 0L,
    val defaultProfileId: Long = 0L,
    val notificationsEnabled: Boolean = true,
    val reminderNotificationsEnabled: Boolean = true,
    val newContentNotificationsEnabled: Boolean = true,
    val analyticsEnabled: Boolean = false
)

class AppPreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(readState())
    val state: StateFlow<AppPreferencesState> = _state.asStateFlow()

    fun setActiveProfile(profileId: Long, makeDefault: Boolean = false) {
        prefs.edit {
            putLong(KEY_ACTIVE_PROFILE_ID, profileId)
            if (makeDefault) {
                putLong(KEY_DEFAULT_PROFILE_ID, profileId)
            }
        }
        _state.value = readState()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled) }
        _state.value = readState()
    }

    fun setReminderNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_REMINDER_NOTIFICATIONS_ENABLED, enabled) }
        _state.value = readState()
    }

    fun setNewContentNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_NEW_CONTENT_NOTIFICATIONS_ENABLED, enabled) }
        _state.value = readState()
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_ANALYTICS_ENABLED, enabled) }
        _state.value = readState()
    }

    var lastKnownCatalogSize: Int
        get() = prefs.getInt(KEY_LAST_KNOWN_CATALOG_SIZE, 0)
        set(value) = prefs.edit { putInt(KEY_LAST_KNOWN_CATALOG_SIZE, value) }

    var lastReminderTimestamp: Long
        get() = prefs.getLong(KEY_LAST_REMINDER_TIMESTAMP, 0L)
        set(value) = prefs.edit { putLong(KEY_LAST_REMINDER_TIMESTAMP, value) }

    private fun readState(): AppPreferencesState = AppPreferencesState(
        activeProfileId = prefs.getLong(KEY_ACTIVE_PROFILE_ID, 0L),
        defaultProfileId = prefs.getLong(KEY_DEFAULT_PROFILE_ID, 0L),
        notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
        reminderNotificationsEnabled = prefs.getBoolean(KEY_REMINDER_NOTIFICATIONS_ENABLED, true),
        newContentNotificationsEnabled = prefs.getBoolean(KEY_NEW_CONTENT_NOTIFICATIONS_ENABLED, true),
        analyticsEnabled = prefs.getBoolean(KEY_ANALYTICS_ENABLED, false)
    )

    companion object {
        private const val PREFS_NAME = "movie_streamer_preferences"
        private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
        private const val KEY_DEFAULT_PROFILE_ID = "default_profile_id"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_REMINDER_NOTIFICATIONS_ENABLED = "reminder_notifications_enabled"
        private const val KEY_NEW_CONTENT_NOTIFICATIONS_ENABLED = "new_content_notifications_enabled"
        private const val KEY_ANALYTICS_ENABLED = "analytics_enabled"
        private const val KEY_LAST_KNOWN_CATALOG_SIZE = "last_known_catalog_size"
        private const val KEY_LAST_REMINDER_TIMESTAMP = "last_reminder_timestamp"
    }
}
