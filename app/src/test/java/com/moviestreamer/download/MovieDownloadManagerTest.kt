package com.moviestreamer.download

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)

class MovieDownloadManagerTest {
    private val context = mockk<Context>(relaxed = true)
    private val downloadManager = mockk<DownloadManager>(relaxed = true)

    init {
        every { context.getSystemService(Context.DOWNLOAD_SERVICE) } returns downloadManager
    }

    @Test
    fun testGetDownloadedFileReturnsNullIfNotExists() {
        every { context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) } returns File("/tmp")
        val mgr = MovieDownloadManager(context)
        val file = mgr.getDownloadedFile("nonexistent")
        assertNull(file)
    }

    @Test
    fun testGetDownloadedFileReturnsFileIfExists() {
        val tempDir = File(System.getProperty("java.io.tmpdir"))
        val testFile = File(tempDir, "test123.mp4")
        testFile.writeText("test")
        every { context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) } returns tempDir
        val mgr = MovieDownloadManager(context)
        val file = mgr.getDownloadedFile("test123")
        assertNotNull(file)
        assertEquals(testFile.absolutePath, file?.absolutePath)
        testFile.delete()
    }
}
