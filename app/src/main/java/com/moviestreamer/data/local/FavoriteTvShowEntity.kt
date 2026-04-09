package com.moviestreamer.data.local

import androidx.room.Entity

@Entity(tableName = "favorite_tv_shows", primaryKeys = ["profileId", "id"])
data class FavoriteTvShowEntity(
    val profileId: Long = 1L,
    val id: Int,
    val name: String,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: String?,
    val voteAverage: Double?
)
