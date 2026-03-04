package com.moviestreamer.data

import com.moviestreamer.data.streaming.StreamingCatalog
import com.moviestreamer.data.streaming.StreamingSource
import com.moviestreamer.data.streaming.StreamingProvider

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
            // Archive.org URLs are unreliable (redirects, rate-limits). Only set
            // videoUrl for verified reliable providers so the UI shows "browsing only"
            // instead of launching the player and immediately failing.
            videoUrl = src.url.takeIf { src.provider != StreamingProvider.ARCHIVE_ORG }
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
            videoUrl = src.url.takeIf { src.provider != StreamingProvider.ARCHIVE_ORG }
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
            // IPTV_FREE streams are currently unverified (404s, redirects).
            // Set videoUrl=null until reliable channels are confirmed working.
            videoUrl = src.streamUrl.takeIf { src.group == "VERIFIED" }
        )
    }
}

