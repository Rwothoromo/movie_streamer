package com.moviestreamer.tv.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("overview")
    val overview: String,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    
    @SerializedName("vote_average")
    val voteAverage: Double,
    
    @SerializedName("release_date")
    val releaseDate: String,
    
    // For demo purposes, we'll use a trailer URL
    val videoUrl: String? = null
) {
    fun getPosterUrl(): String {
        return if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }
    }
    
    fun getBackdropUrl(): String {
        return if (backdropPath != null) {
            "https://image.tmdb.org/t/p/w780$backdropPath"
        } else {
            ""
        }
    }
}

data class MovieResponse(
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("results")
    val results: List<Movie>,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("total_results")
    val totalResults: Int
)
