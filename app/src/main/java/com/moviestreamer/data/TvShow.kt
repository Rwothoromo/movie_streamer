package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class TvShow(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("genres") val genres: List<Genre>? = null,
    @SerializedName("in_production") val inProduction: Boolean? = null,
    val contentType: ContentType = ContentType.TV_SHOW
) {
    fun getPosterUrl(): String? = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    fun getBackdropUrl(): String? = backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" }
}

data class TvShowsResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<TvShow>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)
