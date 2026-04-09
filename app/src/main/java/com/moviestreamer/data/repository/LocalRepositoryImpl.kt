package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.AppPreferencesManager
import com.moviestreamer.data.local.AppPreferencesState
import com.moviestreamer.data.local.ContinueWatchingDao
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.local.FavoriteMovieDao
import com.moviestreamer.data.local.FavoriteMovieEntity
import com.moviestreamer.data.local.FavoriteTvShowDao
import com.moviestreamer.data.local.FavoriteTvShowEntity
import com.moviestreamer.data.local.UserProfileDao
import com.moviestreamer.data.local.UserProfileEntity
import com.moviestreamer.data.local.UserReviewDao
import com.moviestreamer.data.local.UserReviewEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LocalRepositoryImpl(
    private val favoriteMovieDao: FavoriteMovieDao,
    private val favoriteTvShowDao: FavoriteTvShowDao,
    private val continueWatchingDao: ContinueWatchingDao,
    private val userProfileDao: UserProfileDao,
    private val userReviewDao: UserReviewDao,
    private val preferencesManager: AppPreferencesManager
) : LocalRepository {

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        preferencesManager.state.flatMapLatest { state ->
            favoriteMovieDao.getAllFavoriteMovies(resolveProfileId(state))
                .map { entities -> entities.map { it.toMovie() } }
        }

    override suspend fun addFavoriteMovie(movie: Movie) =
        favoriteMovieDao.insertFavoriteMovie(movie.toEntity(currentProfileId()))

    override suspend fun removeFavoriteMovie(movieId: Int) =
        favoriteMovieDao.deleteFavoriteMovie(currentProfileId(), movieId)

    override suspend fun isFavoriteMovie(movieId: Int): Boolean =
        favoriteMovieDao.isFavoriteMovie(currentProfileId(), movieId) > 0

    override fun getFavoriteTvShows(): Flow<List<TvShow>> =
        preferencesManager.state.flatMapLatest { state ->
            favoriteTvShowDao.getAllFavoriteTvShows(resolveProfileId(state))
                .map { entities -> entities.map { it.toTvShow() } }
        }

    override suspend fun addFavoriteTvShow(tvShow: TvShow) =
        favoriteTvShowDao.insertFavoriteTvShow(tvShow.toEntity(currentProfileId()))

    override suspend fun removeFavoriteTvShow(tvShowId: Int) =
        favoriteTvShowDao.deleteFavoriteTvShow(currentProfileId(), tvShowId)

    override suspend fun isFavoriteTvShow(tvShowId: Int): Boolean =
        favoriteTvShowDao.isFavoriteTvShow(currentProfileId(), tvShowId) > 0

    override fun getContinueWatching(): Flow<List<ContinueWatchingEntity>> =
        preferencesManager.state.flatMapLatest { state ->
            continueWatchingDao.getContinueWatching(resolveProfileId(state))
        }

    override suspend fun upsertContinueWatching(item: ContinueWatchingEntity) =
        continueWatchingDao.upsertContinueWatching(item.copy(profileId = currentProfileId()))

    override suspend fun removeContinueWatching(contentId: String) =
        continueWatchingDao.removeContinueWatching(currentProfileId(), contentId)

    override suspend fun getContinueWatchingItem(contentId: String): ContinueWatchingEntity? =
        continueWatchingDao.getContinueWatchingItem(currentProfileId(), contentId)

    override fun getProfiles(): Flow<List<UserProfileEntity>> =
        userProfileDao.getAllProfiles()

    override suspend fun ensureDefaultProfiles() {
        if (userProfileDao.getProfileCount() == 0) {
            val guestId = userProfileDao.insertProfile(
                UserProfileEntity(name = "Guest", avatar = "🎬")
            )
            userProfileDao.insertProfile(
                UserProfileEntity(name = "Kids", avatar = "🧒", isKids = true)
            )
            preferencesManager.setActiveProfile(guestId, makeDefault = true)
            return
        }

        val profiles = userProfileDao.getProfilesSnapshot()
        if (profiles.isNotEmpty()) {
            val state = preferencesManager.state.value
            when {
                state.defaultProfileId == 0L -> {
                    preferencesManager.setActiveProfile(profiles.first().id, makeDefault = true)
                }
                state.activeProfileId == 0L -> {
                    preferencesManager.setActiveProfile(state.defaultProfileId)
                }
            }
        }
    }

    override suspend fun createProfile(name: String, avatar: String, isKids: Boolean): Long {
        val profileId = userProfileDao.insertProfile(
            UserProfileEntity(name = name, avatar = avatar, isKids = isKids)
        )
        if (preferencesManager.state.value.activeProfileId == 0L) {
            preferencesManager.setActiveProfile(profileId, makeDefault = true)
        }
        return profileId
    }

    override fun getAllReviews(): Flow<List<UserReviewEntity>> =
        userReviewDao.getAllReviews()

    override suspend fun saveReview(
        contentId: String,
        contentType: String,
        title: String,
        posterPath: String?,
        rating: Int,
        review: String
    ) {
        userReviewDao.upsertReview(
            UserReviewEntity(
                profileId = currentProfileId(),
                contentId = contentId,
                contentType = contentType,
                title = title,
                posterPath = posterPath,
                rating = rating.coerceIn(1, 5),
                review = review.trim()
            )
        )
    }

    private fun currentProfileId(): Long = resolveProfileId(preferencesManager.state.value)

    private fun resolveProfileId(state: AppPreferencesState): Long =
        state.activeProfileId.takeIf { it > 0 }
            ?: state.defaultProfileId.takeIf { it > 0 }
            ?: 1L

    private fun FavoriteMovieEntity.toMovie() = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = null,
        videoUrl = videoUrl
    )

    private fun Movie.toEntity(profileId: Long) = FavoriteMovieEntity(
        profileId = profileId,
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        videoUrl = videoUrl
    )

    private fun FavoriteTvShowEntity.toTvShow() = TvShow(
        id = id,
        name = name,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = null
    )

    private fun TvShow.toEntity(profileId: Long) = FavoriteTvShowEntity(
        profileId = profileId,
        id = id,
        name = name,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage
    )
}
