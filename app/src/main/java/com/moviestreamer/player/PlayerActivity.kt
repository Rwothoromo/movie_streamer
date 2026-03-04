package com.moviestreamer.player

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.TextView
import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.moviestreamer.R

class PlayerActivity : Activity() {
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var errorTextView: TextView? = null
    private var videoUrl: String? = null
    private var movieTitle: String? = null
    private var playWhenReady = true
    private var playbackPosition = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get video URL from intent
        videoUrl = intent.getStringExtra("VIDEO_URL")
        movieTitle = intent.getStringExtra("MOVIE_TITLE")

        // Validate video URL
        if (videoUrl.isNullOrBlank()) {
            finish()
            return
        }

        // Create player view programmatically
        playerView = PlayerView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = true
            controllerShowTimeoutMs = 10000  // Show controller for 10 seconds
            controllerHideOnTouch = false    // Keep controller visible (important for TV)
            
            // Configure for TV viewing
            setShutterBackgroundColor(android.graphics.Color.BLACK)
        }

        // Create error text view
        errorTextView = TextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            text = ""
            textSize = 18f
            setTextColor(android.graphics.Color.WHITE)
            visibility = View.GONE
        }

        val frameLayout = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.BLACK)
            addView(playerView)
            addView(errorTextView)
        }

        setContentView(frameLayout)

        // Hide system UI for immersive fullscreen
        hideSystemUI()

        initializePlayer()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Use WindowInsetsControllerCompat for consistent behavior across API levels
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.hide(
                        android.view.WindowInsets.Type.statusBars() or
                                android.view.WindowInsets.Type.navigationBars()
                    )
                    controller.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            }
        }
    }

    private fun initializePlayer() {
            // Create ExoPlayer instance
            player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                playerView?.player = exoPlayer

                // Set up the media item
                videoUrl?.let { url ->
                    val mediaItem = MediaItem.fromUri(url)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(playbackPosition)
                }

                // Add listener for playback state
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                // Video ended, could show replay option
                                finish()
                            }

                            Player.STATE_IDLE -> {
                                // Player is idle
                            }

                            Player.STATE_BUFFERING -> {
                                // Player is buffering
                                hideError()
                            }

                            Player.STATE_READY -> {
                                // Player is ready
                                hideError()
                            }
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        showError(getString(R.string.video_error))
                    }
                })
            }
        }

        override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            // Handle D-pad controls
            return when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    player?.let {
                        if (it.isPlaying) {
                            it.pause()
                        } else {
                            it.play()
                        }
                    }
                    true
                }

                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    player?.play()
                    true
                }

                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    player?.pause()
                    true
                }

                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    player?.seekForward()
                    true
                }

                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    player?.seekBack()
                    true
                }

                KeyEvent.KEYCODE_BACK -> {
                    releasePlayer()
                    finish()
                    true
                }

                else -> super.onKeyDown(keyCode, event)
            }
        }

        override fun onStart() {
            super.onStart()
            if (player == null) {
                initializePlayer()
            }
        }

        override fun onStop() {
            super.onStop()
            releasePlayer()
        }

        override fun onDestroy() {
            super.onDestroy()
            // Ensure player is released if not already done in onStop
            releasePlayer()
        }

        private fun releasePlayer() {
            player?.release()
            player = null
        }

        private fun showError(message: String) {
            runOnUiThread {
                errorTextView?.apply {
                    text = message
                    visibility = View.VISIBLE
                }
                playerView?.visibility = View.GONE
            }
        }

        private fun hideError() {
            runOnUiThread {
                errorTextView?.visibility = View.GONE
                playerView?.visibility = View.VISIBLE
            }
        }
    }
