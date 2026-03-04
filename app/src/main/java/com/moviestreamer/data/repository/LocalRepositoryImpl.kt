package com.moviestreamer.data.repository

import com.moviestreamer.data.Movie
import com.moviestreamer.data.TvShow
import com.moviestreamer.data.local.ContinueWatchingDao
import com.moviestreamer.data.local.ContinueWatchingEntity
import com.moviestreamer.data.local.FavoriteMovieDao
import com.moviestreamer.data.local.FavoriteMovieEntity
import com.moviestreamer.data.local.FavoriteTvShowDao
import com.moviestreamer.data.local.FavoriteTvShowEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRepositoryImpl(
    private val favoriteMovieDao: FavoriteMovieDao,
    private val favoriteTvShowDao: FavoriteTvShowDao,
    private val continueWatchingDao: ContinueWatchingDao
) : LocalRepository {

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        favoriteMovieDao.getAllFavoriteMovies().map { entities -> entities.map { it.toMovie() } }

    override suspend fun addFavoriteMovie(movie: Movie) =
        favoriteMovieDao.insertFavoriteMovie(movie.toEntity())

    override suspend fun removeFavoriteMovie(movieId: Int) =
        favoriteMovieDao.deleteFavoriteMovie(movieId)

    override suspend fun isFavoriteMovie(movieId: Int): Boolean =
        favoriteMovieDao.isFavoriteMovie(movieId) > 0

    override fun getFavoriteTvShows(): Flow<List<TvShow>> =
        favoriteTvShowDao.getAllFavoriteTvShows().map { entities -> entities.map { it.toTvShow() } }

    override suspend fun addFavoriteTvShow(tvShow: TvShow) =
        favoriteTvShowDao.insertFavoriteTvShow(tvShow.toEntity())

    override suspend fun removeFavoriteTvShow(tvShowId: Int) =
        favoriteTvShowDao.deleteFavoriteTvShow(tvShowId)

    override suspend fun isFavoriteTvShow(tvShowId: Int): Boolean =
        favoriteTvShowDao.isFavoriteTvShow(tvShowId) > 0

    override fun getContinueWatching(): Flow<List<ContinueWatchingEntity>> =
        continueWatchingDao.getContinueWatching()

    override suspend fun upsertContinueWatching(item: ContinueWatchingEntity) =
        continueWatchingDao.upsertContinueWatching(item)

    override suspend fun removeContinueWatching(contentId: String) =
        continueWatchingDao.removeContinueWatching(contentId)

    override suspend fun getContinueWatchingItem(contentId: String): ContinueWatchingEntity? =
        continueWatchingDao.getContinueWatchingItem(contentId)

    private fun FavoriteMovieEntity.toMovie() = Movie(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        releaseDate = releaseDate, voteAverage = voteAverage, voteCount = null, videoUrl = videoUrl
    )

    private fun Movie.toEntity() = FavoriteMovieEntity(
        id = id, title = title, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        releaseDate = releaseDate, voteAverage = voteAverage, videoUrl = videoUrl
    )

    private fun FavoriteTvShowEntity.toTvShow() = TvShow(
        id = id, name = name, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        firstAirDate = firstAirDate, voteAverage = voteAverage, voteCount = null
    )

    private fun TvShow.toEntity() = FavoriteTvShowEntity(
        id = id, name = name, overview = overview,
        posterPath = posterPath, backdropPath = backdropPath,
        firstAirDate = firstAirDate, voteAverage = voteAverage
    )
}
