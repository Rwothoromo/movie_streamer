package com.moviestreamer.data

import android.content.Context
import com.frostwire.jlibtorrent.AlertListener
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.TorrentInfo
import com.frostwire.jlibtorrent.AddTorrentParams
import java.io.File

/**
 * Manages torrent streaming using jlibtorrent.
 */
class TorrentManager(private val context: Context) {
    private val sessionManager = SessionManager()
    private var torrentHandle: TorrentHandle? = null
    @Volatile private var running = false

    fun startTorrent(
        magnetUri: String,
        downloadDir: File,
        onUpdate: (file: File?, progress: Float, status: String) -> Unit
    ) {
        sessionManager.start()
        running = true
        Thread {
            try {
                onUpdate(null, 0f, "Fetching metadata...")
                // Fetch torrent metadata from magnet
                val torrentBytes = sessionManager.fetchMagnet(magnetUri, 60, downloadDir)
                if (torrentBytes == null || torrentBytes.isEmpty()) {
                    onUpdate(null, 0f, "Failed to fetch torrent metadata")
                    return@Thread
                }
                val torrentInfo = TorrentInfo(torrentBytes)
                // Start download with TorrentInfo
                sessionManager.download(torrentInfo, downloadDir)
                var handle: TorrentHandle? = null
                // Wait for the handle to appear
                while (running && handle == null) {
                    val handles = sessionManager.getTorrentHandles()
                    handle = handles.firstOrNull { it.torrentFile() == torrentInfo }
                    if (handle == null) Thread.sleep(500)
                }
                torrentHandle = handle
                if (!running || handle == null) return@Thread
                val ti = handle.torrentFile()
                val files = ti.files()
                val videoFileIndex = (0 until files.numFiles()).maxByOrNull { files.fileSize(it) } ?: 0
                val videoFile = File(downloadDir, files.fileName(videoFileIndex))
                // Wait for enough data to stream
                while (running) {
                    val progress = handle.status().progress()
                    onUpdate(videoFile, progress.coerceIn(0f, 1f), "Downloading: ${(progress * 100).toInt()}%")
                    if (progress > 0.05f) break // 5% buffer
                    Thread.sleep(500)
                }
                if (!running) return@Thread
                onUpdate(videoFile, 1f, "Ready to play")
            } catch (e: Exception) {
                onUpdate(null, 0f, "Torrent error: ${e.localizedMessage}")
            }
        }.start()
    }

    fun stopTorrent() {
        running = false
        torrentHandle?.let {
            if (it.isValid) {
                it.pause()
                it.flushCache()
            }
        }
        sessionManager.stop()
    }
}
