package com.moviestreamer.download

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class MovieDownloadManager(private val context: Context) {
    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun startDownload(movieId: String, title: String, videoUrl: String): Long {
        val request = DownloadManager.Request(Uri.parse(videoUrl)).apply {
            setTitle(title)
            setDescription("Downloading movie for offline viewing")
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, "$movieId.mp4")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(false)
        }
        return downloadManager.enqueue(request)
    }

    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor? = downloadManager.query(query)
        cursor?.use {
            if (it.moveToFirst()) {
                val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val reason = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                val bytesDownloaded = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal = it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                return DownloadStatus.from(status, reason, bytesDownloaded, bytesTotal)
            }
        }
        return DownloadStatus.Unknown
    }

    fun observeDownloadProgress(downloadId: Long): Flow<DownloadStatus> = flow {
        while (true) {
            emit(getDownloadStatus(downloadId))
            kotlinx.coroutines.delay(500)
            val status = getDownloadStatus(downloadId)
            if (status is DownloadStatus.Success || status is DownloadStatus.Failed) break
        }
    }

    fun getDownloadedFile(movieId: String): File? {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "$movieId.mp4")
        return if (file.exists()) file else null
    }

    fun removeDownload(downloadId: Long, movieId: String) {
        downloadManager.remove(downloadId)
        getDownloadedFile(movieId)?.delete()
    }
}

sealed class DownloadStatus {
    data class Progress(val bytesDownloaded: Long, val bytesTotal: Long): DownloadStatus()
    object Success: DownloadStatus()
    data class Failed(val reason: Int): DownloadStatus()
    object Unknown: DownloadStatus()

    companion object {
        fun from(status: Int, reason: Int, bytesDownloaded: Long, bytesTotal: Long): DownloadStatus {
            return when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> Success
                DownloadManager.STATUS_FAILED -> Failed(reason)
                DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> Progress(bytesDownloaded, bytesTotal)
                else -> Unknown
            }
        }
    }
}
