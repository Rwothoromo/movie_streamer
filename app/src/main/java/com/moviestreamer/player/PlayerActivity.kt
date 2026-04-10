package com.moviestreamer.player

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.moviestreamer.R
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.repository.LocalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File

@androidx.media3.common.util.UnstableApi
class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var playerView: PlayerView? = null
    private var errorLayout: LinearLayout? = null
    private var errorTextView: TextView? = null
    private var skipIntroButton: Button? = null
    private var speedButton: Button? = null
    private var tracksButton: Button? = null
    private var speedOverlay: LinearLayout? = null
    private var tracksOverlay: LinearLayout? = null
    private var nextEpisodeOverlay: LinearLayout? = null
    private var nextEpisodeCountdownView: TextView? = null
    private var bufferingSpinner: ProgressBar? = null

    private var videoUrl: String? = null
    private var movieTitle: String? = null
    private var contentId: String? = null
    private var introEndMs: Long = -1L
    private var nextEpisodeUrl: String? = null
    private var nextEpisodeTitle: String? = null
    private var subtitleUrl: String? = null

    private var playWhenReady = true
    private var playbackPosition = 0L
    private var isPlayerInitializing = false
    private var saveProgressJob: Job? = null
    private var nextEpisodeJob: Job? = null

    private val localRepository: LocalRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE)
        contentId = intent.getStringExtra(EXTRA_CONTENT_ID)
            ?: "content_${videoUrl?.hashCode() ?: 0}"
        introEndMs = intent.getLongExtra(EXTRA_INTRO_END_MS, -1L)
        nextEpisodeUrl = intent.getStringExtra(EXTRA_NEXT_EPISODE_URL)
        nextEpisodeTitle = intent.getStringExtra(EXTRA_NEXT_EPISODE_TITLE)
        subtitleUrl = intent.getStringExtra(EXTRA_SUBTITLE_URL)

        if (videoUrl.isNullOrBlank()) {
            finish()
            return
        }

        buildUI()
        hideSystemUI()

        restoreProgressAndInitialize()
    }

    private fun buildUI() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Player view
        playerView = PlayerView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = true
            controllerShowTimeoutMs = 5000
            controllerHideOnTouch = false
            setShutterBackgroundColor(android.graphics.Color.BLACK)
        }
        root.addView(playerView)

        // Error layout with retry button
        errorLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            visibility = View.GONE
        }
        errorTextView = TextView(this).apply {
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(32, 0, 32, 16)
        }
        val retryButton = Button(this).apply {
            text = getString(R.string.retry)
            setOnClickListener { hideError(); releasePlayer(); initializePlayer() }
        }
        errorLayout!!.addView(errorTextView)
        errorLayout!!.addView(retryButton)
        root.addView(errorLayout)

        // Back button
        val backButton = ImageButton(this).apply {
            layoutParams = FrameLayout.LayoutParams(80, 80).apply {
                gravity = Gravity.TOP or Gravity.START
                setMargins(32, 32, 0, 0)
            }
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setBackgroundColor(android.graphics.Color.argb(180, 0, 0, 0))
            contentDescription = getString(R.string.tv_detail_back)
            setOnClickListener { releasePlayer(); finish() }
        }
        root.addView(backButton)

        // Skip Intro button
        skipIntroButton = Button(this).apply {
            text = getString(R.string.skip_intro)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(0, 0, 48, 160)
            }
            visibility = View.GONE
            setOnClickListener {
                player?.seekTo(introEndMs)
                visibility = View.GONE
            }
        }
        root.addView(skipIntroButton)

        // Speed button
        speedButton = Button(this).apply {
            text = getString(R.string.speed_normal)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 32, 300, 0)
            }
            setOnClickListener { toggleSpeedOverlay() }
        }
        root.addView(speedButton)

        // Tracks (CC/Audio) button
        tracksButton = Button(this).apply {
            text = getString(R.string.tracks_button)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 32, 32, 0)
            }
            visibility = View.GONE
            setOnClickListener { toggleTracksOverlay() }
        }
        root.addView(tracksButton)

        // Speed overlay
        speedOverlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.argb(220, 20, 20, 20))
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 120, 300, 0)
            }
            visibility = View.GONE
            setPadding(8, 8, 8, 8)
        }
        listOf(0.5f to "0.5x", 0.75f to "0.75x", 1.0f to "1x",
               1.25f to "1.25x", 1.5f to "1.5x", 2.0f to "2x").forEach { (speed, label) ->
            Button(this).apply {
                text = label
                setOnClickListener {
                    player?.setPlaybackSpeed(speed)
                    speedButton?.text = label
                    speedOverlay?.visibility = View.GONE
                }
            }.also { speedOverlay!!.addView(it) }
        }
        root.addView(speedOverlay)

        // Tracks overlay (populated dynamically when tracks are known)
        tracksOverlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.argb(220, 20, 20, 20))
            layoutParams = FrameLayout.LayoutParams(320,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setMargins(0, 120, 32, 0)
            }
            visibility = View.GONE
            setPadding(8, 8, 8, 8)
        }
        root.addView(tracksOverlay)

        // Next episode overlay
        nextEpisodeOverlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.argb(200, 0, 0, 0))
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(0, 0, 48, 48)
            }
            visibility = View.GONE
            setPadding(24, 24, 24, 24)
        }
        nextEpisodeCountdownView = TextView(this).apply {
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
        }
        val playNextBtn = Button(this).apply {
            text = getString(R.string.play_next_episode)
            setOnClickListener { playNextEpisode() }
        }
        val cancelNextBtn = Button(this).apply {
            text = getString(R.string.cancel)
            setOnClickListener {
                nextEpisodeJob?.cancel()
                nextEpisodeOverlay?.visibility = View.GONE
            }
        }
        nextEpisodeOverlay!!.addView(nextEpisodeCountdownView)
        nextEpisodeOverlay!!.addView(playNextBtn)
        nextEpisodeOverlay!!.addView(cancelNextBtn)
        root.addView(nextEpisodeOverlay)

        // Buffering spinner
        bufferingSpinner = ProgressBar(this, null, android.R.attr.progressBarStyleLarge).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            visibility = View.GONE
            indeterminateTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.WHITE
            )
        }
        root.addView(bufferingSpinner)

        setContentView(root)
    }

    private fun restoreProgressAndInitialize() {
        if (player != null || isPlayerInitializing || videoUrl.isNullOrBlank()) return
        isPlayerInitializing = true
        lifecycleScope.launch {
            try {
                val saved = contentId?.let { localRepository.getContinueWatchingItem(it) }
                playbackPosition = saved?.progressMs ?: playbackPosition
                if (player == null) {
                    initializePlayer()
                }
            } finally {
                isPlayerInitializing = false
            }
        }
    }

    private fun initializePlayer() {
        playerView?.useController = !isInPictureInPictureMode
        trackSelector = DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setPreferredTextLanguage("en")
                    .setSelectUndeterminedTextLanguage(true)
            )
        }
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector!!)
            .build()
            .also { exoPlayer ->
                playerView?.player = exoPlayer

                videoUrl?.let { url ->
                    val mediaItemBuilder = MediaItem.Builder()
                        .setUri(resolveMediaUri(url))

                    subtitleUrl
                        ?.takeIf { it.isNotBlank() }
                        ?.let { externalSubtitleUrl ->
                            mediaItemBuilder.setSubtitleConfigurations(
                                listOf(
                                    MediaItem.SubtitleConfiguration.Builder(
                                        resolveMediaUri(externalSubtitleUrl)
                                    )
                                        .setMimeType(inferSubtitleMimeType(externalSubtitleUrl))
                                        .setLanguage("en")
                                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                                        .build()
                                )
                            )
                        }

                    exoPlayer.setMediaItem(mediaItemBuilder.build())
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(playbackPosition)
                }

                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        val isBuffering = state == Player.STATE_BUFFERING
                        bufferingSpinner?.visibility = if (isBuffering) View.VISIBLE else View.GONE
                        when (state) {
                            Player.STATE_READY -> {
                                hideError()
                                startSavingProgress()
                                populateTracksOverlay()
                            }
                            Player.STATE_ENDED -> {
                                saveProgressJob?.cancel()
                                if (nextEpisodeUrl != null) showNextEpisodeOverlay()
                                else finish()
                            }
                            else -> {}
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        android.util.Log.e("PlayerActivity",
                            "Playback error for url=$videoUrl | " +
                            "errorCode=${error.errorCode} | cause=${error.cause?.message}", error)
                        val detail = error.cause?.message ?: error.message ?: "unknown"
                        showError("${getString(R.string.video_error)}\n$detail")
                    }

                    override fun onEvents(player: Player, events: Player.Events) {
                        if (events.contains(Player.EVENT_POSITION_DISCONTINUITY) ||
                            events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                            updateSkipIntroVisibility()
                        }
                    }
                })
            }
    }

    private fun resolveMediaUri(value: String): Uri {
        val parsedUri = Uri.parse(value)
        return if (parsedUri.scheme.isNullOrBlank()) {
            Uri.fromFile(File(value))
        } else {
            parsedUri
        }
    }

    private fun inferSubtitleMimeType(value: String): String {
        val lastPathSegment = Uri.parse(value).lastPathSegment.orEmpty()
        return when {
            lastPathSegment.endsWith(".vtt", ignoreCase = true) -> MimeTypes.TEXT_VTT
            lastPathSegment.endsWith(".srt", ignoreCase = true) -> MimeTypes.APPLICATION_SUBRIP
            else -> MimeTypes.APPLICATION_SUBRIP
        }
    }

    private fun startSavingProgress() {
        saveProgressJob?.cancel()
        saveProgressJob = lifecycleScope.launch {
            while (true) {
                delay(5_000)
                saveProgress()
            }
        }
    }

    private suspend fun saveProgress() {
        val p = player ?: return
        val pos = p.currentPosition
        val dur = p.duration.coerceAtLeast(0)
        val cId = contentId ?: return
        val url = videoUrl ?: return
        localRepository.upsertContinueWatching(
            ContinueWatchingEntity(
                contentId = cId,
                title = movieTitle ?: "",
                posterPath = null,
                videoUrl = url,
                progressMs = pos,
                durationMs = dur
            )
        )
    }

    private fun updateSkipIntroVisibility() {
        if (introEndMs <= 0) return
        val pos = player?.currentPosition ?: return
        skipIntroButton?.visibility = if (pos < introEndMs) View.VISIBLE else View.GONE
    }

    private fun populateTracksOverlay() {
        val exo = player ?: return
        val tracks = exo.currentTracks
        tracksOverlay?.removeAllViews()

        var hasOptions = false

        tracks.groups.forEach { group ->
            when (group.type) {
                C.TRACK_TYPE_AUDIO -> {
                    if (group.length > 1) {
                        hasOptions = true
                        addTrackSectionHeader("Audio")
                        for (i in 0 until group.length) {
                            val label = group.getTrackFormat(i).language ?: "Track ${i + 1}"
                            addTrackButton(label) {
                                trackSelector?.setParameters(
                                    trackSelector!!.buildUponParameters()
                                        .setOverrideForType(
                                            TrackSelectionOverride(group.mediaTrackGroup, listOf(i))
                                        )
                                )
                                tracksOverlay?.visibility = View.GONE
                            }
                        }
                    }
                }
                C.TRACK_TYPE_TEXT -> {
                    hasOptions = true
                    addTrackSectionHeader("Subtitles")
                    addTrackButton("Off") {
                        trackSelector?.setParameters(
                            trackSelector!!.buildUponParameters()
                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                        )
                        tracksOverlay?.visibility = View.GONE
                    }
                    for (i in 0 until group.length) {
                        val label = group.getTrackFormat(i).language ?: "Sub ${i + 1}"
                        addTrackButton(label) {
                            trackSelector?.setParameters(
                                trackSelector!!.buildUponParameters()
                                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                    .setOverrideForType(
                                        TrackSelectionOverride(group.mediaTrackGroup, listOf(i))
                                    )
                            )
                            tracksOverlay?.visibility = View.GONE
                        }
                    }
                }
            }
        }

        tracksButton?.visibility = if (hasOptions) View.VISIBLE else View.GONE
    }

    private fun addTrackSectionHeader(label: String) {
        tracksOverlay?.addView(TextView(this).apply {
            text = label
            setTextColor(android.graphics.Color.GRAY)
            textSize = 13f
            setPadding(16, 8, 16, 4)
        })
    }

    private fun addTrackButton(label: String, onClick: () -> Unit) {
        tracksOverlay?.addView(Button(this).apply {
            text = label
            setOnClickListener { onClick() }
        })
    }

    private fun toggleSpeedOverlay() {
        speedOverlay?.visibility = if (speedOverlay?.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        tracksOverlay?.visibility = View.GONE
    }

    private fun toggleTracksOverlay() {
        tracksOverlay?.visibility = if (tracksOverlay?.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        speedOverlay?.visibility = View.GONE
    }

    private fun showNextEpisodeOverlay() {
        nextEpisodeOverlay?.visibility = View.VISIBLE
        nextEpisodeJob?.cancel()
        nextEpisodeJob = lifecycleScope.launch {
            for (i in 5 downTo 1) {
                nextEpisodeCountdownView?.text = getString(R.string.next_episode_countdown, i)
                delay(1_000)
            }
            playNextEpisode()
        }
    }

    private fun playNextEpisode() {
        nextEpisodeJob?.cancel()
        nextEpisodeOverlay?.visibility = View.GONE
        val nextUrl = nextEpisodeUrl ?: return
        videoUrl = nextUrl
        movieTitle = nextEpisodeTitle ?: movieTitle
        contentId = "content_${nextUrl.hashCode()}"
        nextEpisodeUrl = null
        nextEpisodeTitle = null
        subtitleUrl = null
        releasePlayer()
        playbackPosition = 0L
        initializePlayer()
    }

    // Picture-in-Picture
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && player?.isPlaying == true) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
            )
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        // Only call super if API >= 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        playerView?.useController = !isInPictureInPictureMode
        val overlayVisibility = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
        speedButton?.visibility = overlayVisibility
        skipIntroButton?.visibility = if (isInPictureInPictureMode) View.GONE else skipIntroButton?.visibility ?: View.GONE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                player?.let { if (it.isPlaying) it.pause() else it.play() }
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> { player?.play(); true }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> { player?.pause(); true }
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> { player?.seekForward(); true }
            KeyEvent.KEYCODE_MEDIA_REWIND -> { player?.seekBack(); true }
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_BACK -> {
                releasePlayer()
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onStart() {
        super.onStart()
        restoreProgressAndInitialize()
    }

    override fun onStop() {
        super.onStop()
        saveProgressJob?.cancel()
        lifecycleScope.launch { saveProgress() }
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        saveProgressJob?.cancel()
        nextEpisodeJob?.cancel()
        speedOverlay?.visibility = View.GONE
        tracksOverlay?.visibility = View.GONE
        nextEpisodeOverlay?.visibility = View.GONE
        playerView?.hideController()
        playerView?.clearFocus()
        playerView?.useController = false
        playerView?.player = null
        player?.release()
        player = null
        trackSelector = null
    }

    private fun showError(message: String) {
        runOnUiThread {
            errorTextView?.text = message
            errorLayout?.visibility = View.VISIBLE
            playerView?.visibility = View.GONE
        }
    }

    private fun hideError() {
        runOnUiThread {
            errorLayout?.visibility = View.GONE
            playerView?.visibility = View.VISIBLE
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    companion object {
        const val EXTRA_VIDEO_URL = "VIDEO_URL"
        const val EXTRA_MOVIE_TITLE = "MOVIE_TITLE"
        const val EXTRA_SUBTITLE_URL = "SUBTITLE_URL"
        const val EXTRA_CONTENT_ID = "CONTENT_ID"
        const val EXTRA_INTRO_END_MS = "INTRO_END_MS"
        const val EXTRA_NEXT_EPISODE_URL = "NEXT_EPISODE_URL"
        const val EXTRA_NEXT_EPISODE_TITLE = "NEXT_EPISODE_TITLE"
    }
}
