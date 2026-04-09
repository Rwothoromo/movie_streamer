package com.moviestreamer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserReviewDao {
    @Query("SELECT * FROM user_reviews ORDER BY updatedAt DESC")
    fun getAllReviews(): Flow<List<UserReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReview(review: UserReviewEntity)

    @Query("SELECT * FROM user_reviews WHERE contentId = :contentId AND contentType = :contentType ORDER BY updatedAt DESC")
    suspend fun getReviewsForContent(contentId: String, contentType: String): List<UserReviewEntity>
}
