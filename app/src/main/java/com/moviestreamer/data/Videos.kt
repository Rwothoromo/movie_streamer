package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("id")
    val id: String,

    @SerializedName("key")
    val key: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("site")
    val site: String,

    @SerializedName("type")
    val type: String
)

data class VideosResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("results")
    val results: List<Video>
)
