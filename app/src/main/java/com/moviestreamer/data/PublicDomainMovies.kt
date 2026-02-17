package com.moviestreamer.data

object PublicDomainMovies {
    // Sample public domain movies from Archive.org
    // These are classic films in the public domain
    val publicDomainMovies = listOf(
        Movie(
            id = 90001,
            title = "Big Buck Bunny",
            overview = "Big Buck Bunny is a short computer-animated comedy film by the Blender Institute. It is licensed under Creative Commons Attribution 3.0.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2008-04-10",
            voteAverage = 8.5,
            voteCount = 500,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        ),
        Movie(
            id = 90002,
            title = "Elephant's Dream",
            overview = "Elephants Dream is a 2006 short film, the first open-source animated film created by the Orange Open Movie Project.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2006-03-24",
            voteAverage = 7.8,
            voteCount = 300,
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        ),
        Movie(
            id = 90003,
            title = "Nosferatu",
            overview = "A vampire film from 1922, one of the earliest horror films and a masterpiece of German Expressionist cinema.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1922-03-04",
            voteAverage = 8.0,
            voteCount = 800,
            videoUrl = null // Video currently unavailable
        ),
        Movie(
            id = 90004,
            title = "The Cabinet of Dr. Caligari",
            overview = "A landmark of German Expressionist cinema from 1920, telling the story of a hypnotist who uses a somnambulist to commit murders.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1920-02-26",
            voteAverage = 8.0,
            voteCount = 700,
            videoUrl = null // Video currently unavailable
        ),
        Movie(
            id = 90005,
            title = "Metropolis",
            overview = "A groundbreaking science fiction film from 1927 depicting a futuristic city sharply divided between the working class and the city planners.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1927-01-10",
            voteAverage = 8.5,
            voteCount = 1200,
            videoUrl = null // Video currently unavailable
        ),
        Movie(
            id = 90006,
            title = "His Girl Friday",
            overview = "A classic screwball comedy from 1940 about a newspaper editor trying to lure his ex-wife reporter back to work.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1940-01-11",
            voteAverage = 7.8,
            voteCount = 600,
            videoUrl = null // Video currently unavailable
        ),
        Movie(
            id = 90007,
            title = "The Phantom of the Opera",
            overview = "The classic 1925 silent horror film starring Lon Chaney as the deformed Phantom who haunts the Paris Opera House.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1925-09-06",
            voteAverage = 7.6,
            voteCount = 550,
            videoUrl = null // Video currently unavailable
        ),
        Movie(
            id = 90008,
            title = "A Trip to the Moon",
            overview = "Georges Méliès' 1902 pioneering science fiction film about astronomers who travel to the Moon.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1902-09-01",
            voteAverage = 8.2,
            voteCount = 900,
            videoUrl = null // Video currently unavailable
        )
    )
    
}
