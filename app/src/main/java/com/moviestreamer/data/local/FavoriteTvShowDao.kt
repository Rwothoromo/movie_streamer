package com.moviestreamer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTvShowDao {
    @Query("SELECT * FROM favorite_tv_shows")
    fun getAllFavoriteTvShows(): Flow<List<FavoriteTvShowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTvShow(show: FavoriteTvShowEntity)

    @Query("DELETE FROM favorite_tv_shows WHERE id = :tvShowId")
    suspend fun deleteFavoriteTvShow(tvShowId: Int)

    @Query("SELECT COUNT(*) FROM favorite_tv_shows WHERE id = :tvShowId")
    suspend fun isFavoriteTvShow(tvShowId: Int): Int
}
