package com.moviestreamer.ui

import com.moviestreamer.data.Movie
import com.moviestreamer.download.DownloadStatus
import com.moviestreamer.download.MovieDownloadManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import io.mockk.every
import io.mockk.mockk

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDownloadViewModelTest {
    private val movie = Movie(
        id = 42,
        title = "Test Movie",
        overview = "desc",
        posterPath = null,
        backdropPath = null,
        releaseDate = "2020-01-01",
        voteAverage = 7.0,
        voteCount = 10,
        videoUrl = "http://example.com/video.mp4"
    )

    @Test
    fun testCheckDownloadedSuccess() = runTest {
        val mgr = mockk<MovieDownloadManager>()
        every { mgr.getDownloadedFile("42") } returns mockk(relaxed = true)
        val vm = MovieDownloadViewModel(movie, mgr)
        assertEquals(DownloadStatus.Success, vm.uiState.value.status)
    }

    @Test
    fun testCheckDownloadedUnknown() = runTest {
        val mgr = mockk<MovieDownloadManager>()
        every { mgr.getDownloadedFile("42") } returns null
        val vm = MovieDownloadViewModel(movie, mgr)
        assertEquals(DownloadStatus.Unknown, vm.uiState.value.status)
    }
}
