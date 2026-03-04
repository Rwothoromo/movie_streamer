package com.moviestreamer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "continue_watching")
data class ContinueWatchingEntity(
    @PrimaryKey val contentId: String,
    val title: String,
    val posterPath: String?,
    val videoUrl: String,
    val progressMs: Long,
    val durationMs: Long,
    val updatedAt: Long = System.currentTimeMillis()
)
