package com.moviestreamer.domain.usecase

import android.content.Context
import java.io.File
import com.moviestreamer.data.TorrentManager

class StartTorrentStreamUseCase(private val context: Context) {
    private val torrentManager = TorrentManager(context)

    fun execute(
        magnetUri: String,
        downloadDir: File,
        onUpdate: (file: File?, progress: Float, status: String) -> Unit
    ) {
        torrentManager.startTorrent(magnetUri, downloadDir, onUpdate)
    }

    fun stop() {
        torrentManager.stopTorrent()
    }
}
