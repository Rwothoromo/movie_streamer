package com.moviestreamer.data.streaming

/**
 * Abstraction for a playable content source.
 *
 * New providers (IPTV, trailer services, etc.) can be added by implementing
 * this interface without requiring UI changes — the player accepts any [StreamingSource].
 */
sealed class StreamingSource {
    abstract val id: String
    abstract val title: String
    abstract val description: String?
    abstract val posterUrl: String?

    /** A direct HTTP(S) URL playable by ExoPlayer (MP4, HLS .m3u8, DASH .mpd, etc.). */
    data class DirectStream(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val posterUrl: String?,
        val url: String,
        val provider: StreamingProvider = StreamingProvider.ARCHIVE_ORG
    ) : StreamingSource()

    /** An M3U playlist entry (live IPTV channel). */
    data class IptvChannel(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val posterUrl: String?,
        val streamUrl: String,
        val group: String = "General"
    ) : StreamingSource()

    /** A YouTube video (trailer / full film on YouTube). */
    data class YouTubeVideo(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val posterUrl: String?,
        val youtubeId: String
    ) : StreamingSource()
}

enum class StreamingProvider {
    ARCHIVE_ORG,
    WIKIMEDIA,
    IPTV_FREE,
    YOUTUBE,
    OTHER
}
