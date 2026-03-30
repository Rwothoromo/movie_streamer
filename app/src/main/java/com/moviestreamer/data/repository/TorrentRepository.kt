package com.moviestreamer.data.repository

import android.content.Context
import java.io.File

interface TorrentRepository {
    fun startTorrentStream(
        magnetUri: String,
        downloadDir: File,
        onUpdate: (file: File?, progress: Float, status: String) -> Unit
    )
    fun stopTorrentStream()
}

class TorrentRepositoryImpl(private val context: Context) : TorrentRepository {
    private val useCase = com.moviestreamer.domain.usecase.StartTorrentStreamUseCase(context)
    override fun startTorrentStream(
        magnetUri: String,
        downloadDir: File,
        onUpdate: (file: File?, progress: Float, status: String) -> Unit
    ) {
        useCase.execute(magnetUri, downloadDir, onUpdate)
    }
    override fun stopTorrentStream() {
        useCase.stop()
    }
}
