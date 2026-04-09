package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.local.UserProfileEntity
import com.moviestreamer.data.local.UserReviewEntity
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

    fun getProfiles(): Flow<List<UserProfileEntity>>
    suspend fun ensureDefaultProfiles()
    suspend fun createProfile(name: String, avatar: String, isKids: Boolean = false): Long

    fun getAllReviews(): Flow<List<UserReviewEntity>>
    suspend fun saveReview(
        contentId: String,
        contentType: String,
        title: String,
        posterPath: String?,
        rating: Int,
        review: String
    )
}
