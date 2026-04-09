package com.moviestreamer.diagnostics

import android.content.Context
import android.util.Log
import com.moviestreamer.data.local.AppPreferencesManager
import org.json.JSONObject
import java.io.File

class AppDiagnostics(
    private val context: Context,
    private val preferencesManager: AppPreferencesManager
) {
    fun install() {
        val existingHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            recordCrash(thread.name, throwable)
            existingHandler?.uncaughtException(thread, throwable)
        }
    }

    fun trackScreen(screenName: String) {
        trackEvent("screen_view", mapOf("screen" to screenName))
    }

    fun trackEvent(name: String, attributes: Map<String, String> = emptyMap()) {
        if (!preferencesManager.state.value.analyticsEnabled) return
        val json = JSONObject().apply {
            put("event", name)
            put("timestamp", System.currentTimeMillis())
            attributes.forEach { (key, value) -> put(key, value) }
        }
        appendLine("analytics-events.jsonl", json.toString())
        Log.i(TAG, "Tracked event '$name' with $attributes")
    }

    private fun recordCrash(threadName: String, throwable: Throwable) {
        if (!preferencesManager.state.value.analyticsEnabled) return
        val payload = buildString {
            appendLine("thread=$threadName")
            appendLine("timestamp=${System.currentTimeMillis()}")
            appendLine("message=${throwable.message}")
            appendLine(Log.getStackTraceString(throwable))
        }
        appendLine("latest-crash.txt", payload)
        Log.e(TAG, "Captured uncaught exception on $threadName", throwable)
    }

    private fun appendLine(fileName: String, content: String) {
        runCatching {
            val diagnosticsDir = File(context.filesDir, "diagnostics").apply { mkdirs() }
            File(diagnosticsDir, fileName).appendText(content + "\n")
        }.onFailure { error ->
            Log.w(TAG, "Failed to persist diagnostics", error)
        }
    }

    companion object {
        private const val TAG = "AppDiagnostics"
    }
}
