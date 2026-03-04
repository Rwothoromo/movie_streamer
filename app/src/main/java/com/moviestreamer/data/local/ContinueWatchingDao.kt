package com.moviestreamer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContinueWatchingDao {
    @Query("SELECT * FROM continue_watching ORDER BY updatedAt DESC")
    fun getContinueWatching(): Flow<List<ContinueWatchingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContinueWatching(item: ContinueWatchingEntity)

    @Query("DELETE FROM continue_watching WHERE contentId = :contentId")
    suspend fun removeContinueWatching(contentId: String)

    @Query("SELECT * FROM continue_watching WHERE contentId = :contentId LIMIT 1")
    suspend fun getContinueWatchingItem(contentId: String): ContinueWatchingEntity?
}
