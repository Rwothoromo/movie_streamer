package com.moviestreamer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movies WHERE profileId = :profileId ORDER BY title ASC")
    fun getAllFavoriteMovies(profileId: Long): Flow<List<FavoriteMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE profileId = :profileId AND id = :movieId")
    suspend fun deleteFavoriteMovie(profileId: Long, movieId: Int)

    @Query("SELECT COUNT(*) FROM favorite_movies WHERE profileId = :profileId AND id = :movieId")
    suspend fun isFavoriteMovie(profileId: Long, movieId: Int): Int
}
