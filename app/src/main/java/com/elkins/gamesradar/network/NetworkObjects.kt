package com.elkins.gamesradar.network

import com.elkins.gamesradar.database.DatabaseGame
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/* Response class for calls to the "games" endpoint */
@JsonClass(generateAdapter = true)
data class GamesResponse(
    @Json(name = "status_code")val statusCode: Int,
    val error: String?,
    @Json(name = "number_of_total_results") val totalResults: Int,
    @Json(name = "number_of_page_results") val totalPages: Int,
    val limit: Int,
    val offset: Int,
    val results: MutableList<NetworkGame>
)

/* Individual game from the results of the "games' response */
@JsonClass(generateAdapter = true)
data class NetworkGame(
    val id: Long,
    val guid: String,
    val name: String,
    val platforms: List<String>?, // TODO create enum class
    val image: NetworkImage?,
    @Json(name = "original_release_date") val originalReleaseDate: String?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?
)

fun NetworkGame.asDatabaseModel(): DatabaseGame {
    return DatabaseGame(
        id = id,
        guid = guid,
        name = name,
        platforms = platforms,
        imageUrl = image!!.originalUrl
    )
}

/* Network class containing links for a game's images */
@JsonClass(generateAdapter = true)
data class NetworkImage(
    @Json(name = "icon_url") val iconUrl: String?,
    @Json(name = "medium_url") val mediumUrl: String?,
    @Json(name = "screen_url") val screenUrl: String?,
    @Json(name = "screen_large_url") val screenLargeUrl: String?,
    @Json(name = "small_url") val smallUrl: String?,
    @Json(name = "super_url") val superUrl: String?,
    @Json(name = "thumb_url") val thumbUrl: String?,
    @Json(name = "tiny_url") val tinyUrl: String?,
    @Json(name = "original_url") val originalUrl: String?
)