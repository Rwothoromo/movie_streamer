package com.moviestreamer.data

object PublicDomainMovies {
    // Sample public domain movies from Archive.org
    // These are classic films in the public domain
    val publicDomainMovies = listOf(
        Movie(
            id = 90001,
            title = "Night of the Living Dead",
            overview = "A classic horror film about zombies attacking a farmhouse. This iconic 1968 film is in the public domain.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1968-10-01",
            voteAverage = 7.5,
            voteCount = 1000,
            videoUrl = "https://archive.org/download/night_of_the_living_dead/night_of_the_living_dead_512kb.mp4"
        ),
        Movie(
            id = 90002,
            title = "Plan 9 from Outer Space",
            overview = "Aliens resurrect dead humans as zombies to stop humanity from creating a doomsday weapon. A cult classic from 1959.",
            posterPath = null,
            backdropPath = null,
            releaseDate = "1959-07-22",
            voteAverage = 4.0,
            voteCount = 500,
            videoUrl = "https://archive.org/download/Plan9FromOuterSpace/Plan_9_from_Outer_Space_512kb.mp4"
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
            videoUrl = "https://archive.org/download/nosferatu_een_symfonie_des_grauens/nosferatu_een_symfonie_des_grauens_512kb.mp4"
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
            videoUrl = "https://archive.org/download/TheCabinetOfDr.Caligari/TheCabinetOfDr.Caligari_512kb.mp4"
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
            videoUrl = "https://archive.org/download/Metropolis_20100119/metropolis_512kb.mp4"
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
            videoUrl = "https://archive.org/download/His_Girl_Friday/His_Girl_Friday_512kb.mp4"
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
            videoUrl = "https://archive.org/download/the_phantom_of_the_opera/the_phantom_of_the_opera_512kb.mp4"
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
            videoUrl = "https://archive.org/download/Le_Voyage_dans_la_lune/Le_Voyage_dans_la_lune_512kb.mp4"
        )
    )
    
    fun getPublicDomainMovies(): List<Movie> = publicDomainMovies
}
