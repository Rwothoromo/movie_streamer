package com.moviestreamer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviestreamer.data.Movie
import com.moviestreamer.download.MovieDownloadManager
import com.moviestreamer.download.DownloadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class MovieDownloadUiState(
    val downloadId: Long? = null,
    val status: DownloadStatus = DownloadStatus.Unknown,
    val errorMessage: String? = null
)

class MovieDownloadViewModel(
    private val movie: Movie,
    private val downloadManager: MovieDownloadManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieDownloadUiState())
    val uiState: StateFlow<MovieDownloadUiState> = _uiState.asStateFlow()

    init {
        checkDownloaded()
    }

    fun startDownload() {
        if (movie.videoUrl == null) return
        try {
            val id = downloadManager.startDownload(movie.id.toString(), movie.title, movie.videoUrl)
            _uiState.update { it.copy(downloadId = id, status = DownloadStatus.Progress(0, 0), errorMessage = null) }
            observeProgress(id)
        } catch (e: Exception) {
            _uiState.update { it.copy(status = DownloadStatus.Failed(-1), errorMessage = e.message) }
        }
    }

    fun removeDownload() {
        try {
            _uiState.value.downloadId?.let { id ->
                downloadManager.removeDownload(id, movie.id.toString())
            }
            _uiState.update { it.copy(downloadId = null, status = DownloadStatus.Unknown, errorMessage = null) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    private fun checkDownloaded() {
        val file = downloadManager.getDownloadedFile(movie.id.toString())
        if (file != null) {
            _uiState.update { it.copy(status = DownloadStatus.Success, errorMessage = null) }
        } else {
            _uiState.update { it.copy(status = DownloadStatus.Unknown, errorMessage = null) }
        }
    }

    private fun observeProgress(downloadId: Long) {
        viewModelScope.launch {
            try {
                downloadManager.observeDownloadProgress(downloadId).collect { status ->
                    if (status is DownloadStatus.Success) {
                        // Double-check file existence
                        val file = downloadManager.getDownloadedFile(movie.id.toString())
                        if (file == null) {
                            _uiState.update { it.copy(status = DownloadStatus.Failed(-2), errorMessage = "File missing after download.") }
                        } else {
                            _uiState.update { it.copy(status = status, errorMessage = null) }
                        }
                    } else if (status is DownloadStatus.Failed) {
                        _uiState.update { it.copy(status = status, errorMessage = "Download failed.") }
                    } else {
                        _uiState.update { it.copy(status = status, errorMessage = null) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(status = DownloadStatus.Failed(-3), errorMessage = e.message) }
            }
        }
    }
}
