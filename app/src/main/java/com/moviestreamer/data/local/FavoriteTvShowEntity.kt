package com.moviestreamer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tv_shows")
data class FavoriteTvShowEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: String?,
    val voteAverage: Double?
)
