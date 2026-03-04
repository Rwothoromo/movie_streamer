package com.moviestreamer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteMovieEntity::class, FavoriteTvShowEntity::class, ContinueWatchingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun favoriteTvShowDao(): FavoriteTvShowDao
    abstract fun continueWatchingDao(): ContinueWatchingDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "movie_streamer.db").build()
    }
}
