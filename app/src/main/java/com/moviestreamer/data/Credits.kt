package com.moviestreamer.data

import com.google.gson.annotations.SerializedName

data class Cast(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("character")
    val character: String?,

    @SerializedName("profile_path")
    val profilePath: String?
) {
    fun getProfileUrl(): String? {
        return profilePath?.let { "https://image.tmdb.org/t/p/w185$it" }
    }
}

data class Crew(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("job")
    val job: String?,

    @SerializedName("department")
    val department: String?,

    @SerializedName("profile_path")
    val profilePath: String?
) {
    fun getProfileUrl(): String? {
        return profilePath?.let { "https://image.tmdb.org/t/p/w185$it" }
    }
}

data class CreditsResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("cast")
    val cast: List<Cast>,

    @SerializedName("crew")
    val crew: List<Crew>
)
