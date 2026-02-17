package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("overview")
    val overview: String?,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    
    @SerializedName("release_date")
    val releaseDate: String?,
    
    @SerializedName("vote_average")
    val voteAverage: Double?,
    
    @SerializedName("vote_count")
    val voteCount: Int?,
    
    // For our purposes, we'll use Archive.org URLs for actual playback
    var videoUrl: String? = null
) {
    fun getPosterUrl(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }
    
    fun getBackdropUrl(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" }
    }
}

data class MoviesResponse(
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("results")
    val results: List<Movie>,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("total_results")
    val totalResults: Int
)
