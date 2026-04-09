package com.moviestreamer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContinueWatchingDao {
    @Query("SELECT * FROM continue_watching WHERE profileId = :profileId ORDER BY updatedAt DESC")
    fun getContinueWatching(profileId: Long): Flow<List<ContinueWatchingEntity>>

    @Query("SELECT * FROM continue_watching WHERE profileId = :profileId ORDER BY updatedAt DESC")
    suspend fun getContinueWatchingSnapshot(profileId: Long): List<ContinueWatchingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertContinueWatching(item: ContinueWatchingEntity)

    @Query("DELETE FROM continue_watching WHERE profileId = :profileId AND contentId = :contentId")
    suspend fun removeContinueWatching(profileId: Long, contentId: String)

    @Query("SELECT * FROM continue_watching WHERE profileId = :profileId AND contentId = :contentId LIMIT 1")
    suspend fun getContinueWatchingItem(profileId: Long, contentId: String): ContinueWatchingEntity?
}
