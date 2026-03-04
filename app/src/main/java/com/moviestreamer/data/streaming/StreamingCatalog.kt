package com.moviestreamer.data.streaming

import com.moviestreamer.data.streaming.StreamingSource.DirectStream
import com.moviestreamer.data.streaming.StreamingSource.IptvChannel

/**
 * Hard-coded catalog of free, legal streaming content.
 *
 * All Archive.org items are verified public domain or Creative Commons licensed.
 * All IPTV entries are publicly accessible free streams.
 * Legal status documented inline per source.
 */
object StreamingCatalog {

    // -------------------------------------------------------------------------
    // Public Domain & Open-License Films (Archive.org / Wikimedia Commons)
    // Legal basis: US copyright expired (pre-1928) or Creative Commons license
    // -------------------------------------------------------------------------
    val publicDomainFilms: List<DirectStream> = listOf(
        DirectStream(
            id = "pd_big_buck_bunny",
            title = "Big Buck Bunny",
            description = "Blender Institute short film (2008). Creative Commons Attribution 3.0.",
            posterUrl = null,
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_elephants_dream",
            title = "Elephant's Dream",
            description = "First open-source animated film (2006). Creative Commons Attribution 2.5.",
            posterUrl = null,
            url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_nosferatu",
            title = "Nosferatu (1922)",
            description = "Landmark German Expressionist vampire film by F. W. Murnau. Public domain (pre-1928).",
            posterUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Nosferatu%2C_eine_Symphonie_des_Grauens_%281922%29.jpg/500px-Nosferatu%2C_eine_Symphonie_des_Grauens_%281922%29.jpg",
            url = "https://archive.org/download/nosferatu-eng./Nosferatu%20(Eng)..mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_cabinet_dr_caligari",
            title = "The Cabinet of Dr. Caligari (1920)",
            description = "German Expressionist silent film by Robert Wiene. Public domain (pre-1928).",
            posterUrl = null,
            url = "https://archive.org/download/the-cabinet-of-dr.-caligari-1920-full-movie/The%20Cabinet%20of%20Dr.%20Caligari%20(1920)%20FULL%20MOVIE.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_metropolis",
            title = "Metropolis (1927)",
            description = "Fritz Lang's groundbreaking science fiction epic. Public domain (pre-1928).",
            posterUrl = null,
            url = "https://archive.org/download/metropolis-1927-4-k-u-rnemls-bwry/Metropolis%20(1927)%204K%20%5BuRNEmlsBWRY%5D.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_his_girl_friday",
            title = "His Girl Friday (1940)",
            description = "Classic screwball comedy by Howard Hawks. US public domain (copyright not renewed).",
            posterUrl = null,
            url = "https://archive.org/download/HisGirlFriday/MoviePowderPresentsHisGirlFriday_512kb.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_trip_to_moon",
            title = "A Trip to the Moon (1902)",
            description = "Georges Méliès' pioneering science fiction film. Public domain (pre-1928).",
            posterUrl = null,
            url = "https://archive.org/download/TripToTheMoon/TripToTheMoon_512kb.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_phantom_opera",
            title = "The Phantom of the Opera (1925)",
            description = "Classic silent horror film starring Lon Chaney. Public domain (pre-1928).",
            posterUrl = null,
            url = "https://archive.org/download/thephantomoftheoperalonchaneyversion1929cutbfirelease/The%20Phantom%20Of%20The%20Opera%20(Lon%20Chaney%20Version%2C%201929%20Cut)%20(BFI%20Release).mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_general",
            title = "The General (1926)",
            description = "Buster Keaton's acclaimed Civil War comedy. Public domain (pre-1928).",
            posterUrl = null,
            url = "https://archive.org/download/The_General_Buster_Keaton/The_General_512kb.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "pd_birth_of_nation",
            title = "Night of the Living Dead (1968)",
            description = "George Romero's seminal zombie horror film. Public domain (copyright formalities not met).",
            posterUrl = null,
            url = "https://archive.org/download/Night_Of_The_Living_Dead_raw_HD_WS/NightOfTheLivingDeadWSInternationalPrint.mp4",
            provider = StreamingProvider.OTHER
        )
    )

    // -------------------------------------------------------------------------
    // Public Domain TV Episodes (Archive.org)
    // Legal basis: US copyright expired or not renewed
    // -------------------------------------------------------------------------
    val publicDomainTvEpisodes: List<DirectStream> = listOf(
        DirectStream(
            id = "tv_cisco_kid_s1e1",
            title = "The Cisco Kid — \"The Badman\" (1950)",
            description = "Classic Western TV series. Episode 1, Season 1. Public domain (copyright not renewed).",
            posterUrl = null,
            url = "https://archive.org/download/cisco-kid-s-01-e-08-convict-story/Cisco%20Kid_S01E01_Bates%20Story.mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "tv_science_fiction_theatre_s1e1",
            title = "Science Fiction Theatre — \"Time Is Just a Place\" (1955)",
            description = "Classic US sci-fi anthology series. Public domain (copyright not renewed).",
            posterUrl = null,
            url = "https://archive.org/download/science-fiction-theatre-x-4-season-1-1955/Science%20Fiction%20Theatre%20x%204%20Season%201%20(1955).mp4",
            provider = StreamingProvider.OTHER
        ),
        DirectStream(
            id = "tv_rocky_jones_s1e1",
            title = "Rocky Jones, Space Ranger — \"Beyond the Curtain of Space\" (1954)",
            description = "Classic US science fiction TV series. Public domain (copyright not renewed).",
            posterUrl = null,
            url = "https://archive.org/download/rocky-jones-beyond-the-curtain-of-space/Rocky%20Jones%20-%20Beyond%20the%20Curtain%20of%20Space.mp4",
            provider = StreamingProvider.OTHER
        )
    )

    // -------------------------------------------------------------------------
    // Free IPTV Channels (publicly accessible streams)
    // Legal basis: These are publicly available broadcast streams
    // Sources: IPTV-org free channel lists / public broadcaster streams
    // -------------------------------------------------------------------------
    val freeIptvChannels: List<IptvChannel> = listOf(
        IptvChannel(
            id = "iptv_bloomberg",
            title = "Bloomberg TV",
            description = "24-hour global business and financial news.",
            posterUrl = null,
            streamUrl = "https://dai.google.com/linear/hls/event/Sid4xiTQTkCT1SLu6rjUSQ/master.m3u8",
            group = "News"
        ),
        IptvChannel(
            id = "iptv_nasa_tv",
            title = "NASA TV",
            description = "Official NASA television channel with live launches, missions and educational content.",
            posterUrl = null,
            streamUrl = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-ARC/master.m3u8",
            group = "Science & Education"
        ),
        IptvChannel(
            id = "iptv_pbskids",
            title = "PBS Kids",
            description = "Free children's educational programming from PBS.",
            posterUrl = null,
            streamUrl = "https://pbskids.l3.dish.com/hls/live/571455/PBSKIDS/master.m3u8",
            group = "Kids"
        )
    )
}
