package com.moviestreamer.data.local

import androidx.room.Entity

@Entity(tableName = "continue_watching", primaryKeys = ["profileId", "contentId"])
data class ContinueWatchingEntity(
    val profileId: Long = 1L,
    val contentId: String,
    val title: String,
    val posterPath: String?,
    val videoUrl: String,
    val progressMs: Long,
    val durationMs: Long,
    val updatedAt: Long = System.currentTimeMillis()
)
