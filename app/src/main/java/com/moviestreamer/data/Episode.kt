package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("runtime") val runtime: Int?
) {
    fun getStillUrl(): String? = stillPath?.let { "https://image.tmdb.org/t/p/w300$it" }
}
