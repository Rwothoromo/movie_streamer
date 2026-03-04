package com.moviestreamer.data

import com.moviestreamer.data.streaming.StreamingCatalog
import com.moviestreamer.data.streaming.StreamingSource

object PublicDomainMovies {
    /**
     * All public domain / open-license films mapped to [Movie] for display.
     * Source: [StreamingCatalog.publicDomainFilms] — Archive.org and open-license sources.
     */
    val publicDomainMovies: List<Movie> = StreamingCatalog.publicDomainFilms.mapIndexed { index, src ->
        Movie(
            id = 90000 + index,
            title = src.title,
            overview = src.description,
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = null,
            voteCount = null,
            videoUrl = src.url
        )
    }

    /**
     * Public domain TV episodes mapped to [Movie] for display convenience.
     * Source: [StreamingCatalog.publicDomainTvEpisodes] — Archive.org.
     */
    val publicDomainTvEpisodes: List<Movie> = StreamingCatalog.publicDomainTvEpisodes.mapIndexed { index, src ->
        Movie(
            id = 91000 + index,
            title = src.title,
            overview = src.description,
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = null,
            voteCount = null,
            videoUrl = src.url
        )
    }

    /**
     * Free IPTV live channels mapped to [Movie] for display.
     * Source: [StreamingCatalog.freeIptvChannels] — publicly accessible broadcast streams.
     */
    val freeIptvChannels: List<Movie> = StreamingCatalog.freeIptvChannels.mapIndexed { index, src ->
        Movie(
            id = 92000 + index,
            title = src.title,
            overview = src.description,
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = null,
            voteCount = null,
            videoUrl = src.streamUrl
        )
    }
}

