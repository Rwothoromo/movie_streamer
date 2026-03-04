package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Season(
    @SerializedName("id") val id: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    @SerializedName("episodes") val episodes: List<Episode>? = null
) {
    fun getPosterUrl(): String? = posterPath?.let { "https://image.tmdb.org/t/p/w300$it" }
}
