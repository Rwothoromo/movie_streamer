package com.moviestreamer.data.local

import androidx.room.Entity

@Entity(tableName = "favorite_movies", primaryKeys = ["profileId", "id"])
data class FavoriteMovieEntity(
    val profileId: Long = 1L,
    val id: Int,
    val title: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val videoUrl: String?
)
