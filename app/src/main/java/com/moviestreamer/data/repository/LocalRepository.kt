package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingEntity
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun addFavoriteMovie(movie: Movie)
    suspend fun removeFavoriteMovie(movieId: Int)
    suspend fun isFavoriteMovie(movieId: Int): Boolean

    fun getFavoriteTvShows(): Flow<List<TvShow>>
    suspend fun addFavoriteTvShow(tvShow: TvShow)
    suspend fun removeFavoriteTvShow(tvShowId: Int)
    suspend fun isFavoriteTvShow(tvShowId: Int): Boolean

    fun getContinueWatching(): Flow<List<ContinueWatchingEntity>>
    suspend fun upsertContinueWatching(item: ContinueWatchingEntity)
    suspend fun removeContinueWatching(contentId: String)
    suspend fun getContinueWatchingItem(contentId: String): ContinueWatchingEntity?
}
