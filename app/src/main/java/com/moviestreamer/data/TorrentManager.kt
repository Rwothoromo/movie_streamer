package com.moviestreamer.data

import android.content.Context
import android.util.Log
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.TorrentInfo
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
        if (magnetUri.isBlank()) {
            onUpdate(null, 0f, "Enter a magnet URI")
            return
        }

        stopTorrent()
        ensureSessionStarted()
        running = true

        Thread {
            try {
                val normalizedMagnetUri = magnetUri.trim()
                onUpdate(null, 0f, "Fetching metadata...")
                sessionManager.reopenNetworkSockets()

                val torrentBytes = sessionManager.fetchMagnet(normalizedMagnetUri, 90, downloadDir)
                if (torrentBytes == null || torrentBytes.isEmpty()) {
                    val alertMessage = sessionManager.lastAlertError()?.localizedMessage?.takeIf { it.isNotBlank() }
                    val emulatorHint = if (isLikelyEmulator()) {
                        " Torrent streaming can also fail on the emulator network stack."
                    } else {
                        ""
                    }
                    onUpdate(
                        null,
                        0f,
                        alertMessage?.let { "Failed to fetch torrent metadata: $it" }
                            ?: "Failed to fetch torrent metadata. Check the magnet link and try again.$emulatorHint"
                    )
                    return@Thread
                }

                val torrentInfo = TorrentInfo(torrentBytes)
                sessionManager.download(torrentInfo, downloadDir)

                var handle: TorrentHandle? = null
                var attempts = 0
                while (running && (handle == null || !handle.isValid) && attempts < 40) {
                    handle = sessionManager.find(torrentInfo)
                    if (handle?.isValid == true) {
                        break
                    }
                    attempts++
                    Thread.sleep(500)
                }

                torrentHandle = handle
                if (!running || handle?.isValid != true) {
                    onUpdate(null, 0f, "Torrent added but is not ready yet. Please try again.")
                    return@Thread
                }

                val torrentFile = handle.torrentFile()
                val files = torrentFile.files()
                val videoFileIndex = (0 until files.numFiles()).maxByOrNull { files.fileSize(it) } ?: 0
                val videoFile = File(downloadDir, files.fileName(videoFileIndex))

                while (running) {
                    val progress = handle.status().progress().coerceIn(0f, 1f)
                    onUpdate(videoFile, progress, "Downloading: ${(progress * 100).toInt()}%")
                    if (progress > 0.05f || (videoFile.exists() && videoFile.length() > 0L)) {
                        break
                    }
                    Thread.sleep(500)
                }

                if (!running) return@Thread
                onUpdate(videoFile, 1f, "Ready to play")
            } catch (e: Exception) {
                Log.e("TorrentManager", "Failed to start torrent", e)
                onUpdate(null, 0f, "Torrent error: ${e.localizedMessage ?: "unknown error"}")
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
        torrentHandle = null
        if (sessionManager.isRunning) {
            sessionManager.stop()
        }
    }

    private fun ensureSessionStarted() {
        if (sessionManager.isRunning) return

        val settings = SettingsPack()
            .listenInterfaces("0.0.0.0:6881")
            .connectionsLimit(50)
            .activeDownloads(1)
            .activeSeeds(1)
            .activeChecking(1)
            .alertQueueSize(2000)
            .stopTrackerTimeout(10)

        settings.setEnableDht(true)
        settings.setEnableLsd(false)
        settings.setDhtBootstrapNodes(
            "dht.libtorrent.org:25401,router.bittorrent.com:6881,router.utorrent.com:6881,dht.transmissionbt.com:6881"
        )

        sessionManager.start(SessionParams(settings))
    }

    private fun isLikelyEmulator(): Boolean {
        val fingerprint = android.os.Build.FINGERPRINT.orEmpty()
        val model = android.os.Build.MODEL.orEmpty()
        return fingerprint.contains("generic", ignoreCase = true) ||
            fingerprint.contains("emulator", ignoreCase = true) ||
            model.contains("Emulator", ignoreCase = true)
    }
}
