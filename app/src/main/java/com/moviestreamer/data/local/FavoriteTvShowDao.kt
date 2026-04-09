package com.moviestreamer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTvShowDao {
    @Query("SELECT * FROM favorite_tv_shows WHERE profileId = :profileId ORDER BY name ASC")
    fun getAllFavoriteTvShows(profileId: Long): Flow<List<FavoriteTvShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTvShow(show: FavoriteTvShowEntity)

    @Query("DELETE FROM favorite_tv_shows WHERE profileId = :profileId AND id = :tvShowId")
    suspend fun deleteFavoriteTvShow(profileId: Long, tvShowId: Int)

    @Query("SELECT COUNT(*) FROM favorite_tv_shows WHERE profileId = :profileId AND id = :tvShowId")
    suspend fun isFavoriteTvShow(profileId: Long, tvShowId: Int): Int
}
