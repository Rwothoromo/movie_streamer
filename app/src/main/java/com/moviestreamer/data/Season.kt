package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Season(
    @SerializedName("id")
    val id: Int,

    @SerializedName("season_number")
    val seasonNumber: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("air_date")
    val airDate: String?,

    @SerializedName("episodes")
    val episodes: List<Episode>?
) {
    fun getPosterUrl(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }
}

data class Episode(
    @SerializedName("id")
    val id: Int,

    @SerializedName("episode_number")
    val episodeNumber: Int,

    @SerializedName("season_number")
    val seasonNumber: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("air_date")
    val airDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double?,

    @SerializedName("still_path")
    val stillPath: String?
) {
    fun getStillUrl(): String? {
        return stillPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }
}
