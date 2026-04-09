package com.moviestreamer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatar: String,
    val isKids: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
