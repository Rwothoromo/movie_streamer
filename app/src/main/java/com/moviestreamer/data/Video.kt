package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("id") val id: String,
    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("site") val site: String,
    @SerializedName("type") val type: String,
    @SerializedName("official") val official: Boolean?
) {
    fun isYouTubeTrailer(): Boolean = site == "YouTube" && type == "Trailer"
    fun getYouTubeThumbnailUrl(): String? =
        if (site == "YouTube") "https://img.youtube.com/vi/$key/hqdefault.jpg" else null
}

data class VideosResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: List<Video>
)
