package com.moviestreamer.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_reviews",
    indices = [Index(value = ["profileId", "contentId", "contentType"], unique = true)]
)
data class UserReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val contentId: String,
    val contentType: String,
    val title: String,
    val posterPath: String?,
    val rating: Int,
    val review: String,
    val updatedAt: Long = System.currentTimeMillis()
)
