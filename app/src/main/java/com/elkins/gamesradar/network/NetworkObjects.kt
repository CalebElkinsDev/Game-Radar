package com.elkins.gamesradar.network

import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.gamedetails.GameDetails
import com.elkins.gamesradar.utility.DateUtilities.Companion.networkDateStringToDate
import com.elkins.gamesradar.utility.calculateReleaseTimeInMillis
import com.elkins.gamesradar.utility.timeInMillis
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/** Response class for calls to the "games" endpoint of the API */
@JsonClass(generateAdapter = true)
data class GamesResponse(
    @Json(name = "status_code")val statusCode: Int,
    val error: String?,
    @Json(name = "number_of_total_results") val totalResults: Int,
    @Json(name = "number_of_page_results") val totalPages: Int,
    val limit: Int,
    val offset: Int,
    val results: List<NetworkGame>
)

/** Individual game from the results of the [GamesResponse] */
@JsonClass(generateAdapter = true)
data class NetworkGame(
    val id: Long,
    val guid: String,
    val name: String,
    val platforms: List<NetworkPlatform>?,
    val image: NetworkImage?,
    @Json(name = "original_release_date") val originalReleaseDate: String?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?
)

/**
 * Similar to the [GamesResponse] network object, but returns a single result of
 * [NetworkGameDetail]. Used with the "game" endpoint of the API.
 */
@JsonClass(generateAdapter = true)
data class GameResponse(
    @Json(name = "status_code")val statusCode: Int,
    val error: String?,
    @Json(name = "number_of_total_results") val totalResults: Int,
    @Json(name = "number_of_page_results") val totalPages: Int,
    val limit: Int,
    val offset: Int,
    val results: NetworkGameDetail
)

/** Class containing information for a game's details. Contains the same fields as [NetworkGame]
 * plus additional ones. */
@JsonClass(generateAdapter = true)
data class NetworkGameDetail(
    val id: Long,
    val guid: String,
    val name: String,
    val platforms: List<NetworkPlatform>?,
    val image: NetworkImage?,
    val images: List<NetworkImageSimple>?,
    @Json(name = "original_release_date") val originalReleaseDate: String?,
    @Json(name = "expected_release_year") val expectedReleaseYear: Int?,
    @Json(name = "expected_release_quarter") val expectedReleaseQuarter: Int?,
    @Json(name = "expected_release_month") val expectedReleaseMonth: Int?,
    @Json(name = "expected_release_day") val expectedReleaseDay: Int?,
    val deck: String?,
    val description: String?,
    val developers: List<GenericObject>?,
    val genres: List<GenericObject>?,
    val publishers: List<GenericObject>?,
)

/** Used by several fields to contain basic information*/
@JsonClass(generateAdapter = true)
data class GenericObject(
    @Json(name = "api_detail_url") val apiDetailUrl: String?,
    val id: Int?,
    val name: String,
    @Json(name = "site_detail_url") val siteDetailUrl: String?
)

fun NetworkGame.asDatabaseModel(): DatabaseGame {
    return DatabaseGame(
        id = id,
        guid = guid,
        name = name,
        platforms = platforms?.map {
            it.abbreviation
        },
        imageUrl = image!!.originalUrl,
        originalReleaseDate = networkDateStringToDate(originalReleaseDate).timeInMillis(),
        expectedReleaseYear = expectedReleaseYear ?: -1,
        expectedReleaseQuarter = expectedReleaseQuarter ?: -1,
        expectedReleaseMonth = expectedReleaseMonth?.plus(1) ?: -1,
        expectedReleaseDay = expectedReleaseDay ?: -1,
        releaseDateInMillis = calculateReleaseTimeInMillis()
    )
}

fun NetworkGameDetail.asDomainModel(): GameDetails {
    return GameDetails(
        id = id,
        guid = guid,
        name = name,
        platforms = platforms?.map { it.abbreviation },
        imageUrl = image!!.originalUrl,
        images = images?.map { it.originalUrl },
        originalReleaseDate = networkDateStringToDate(originalReleaseDate).timeInMillis(),
        expectedReleaseYear = expectedReleaseYear ?: -1,
        expectedReleaseQuarter = expectedReleaseQuarter ?: -1,
        expectedReleaseMonth = expectedReleaseMonth?.plus(1) ?: -1,
        expectedReleaseDay = expectedReleaseDay ?: -1,
        deck = deck,
        description = description,
        genres = genres?.map { it.name },
        developers = developers?.map { it.name },
        publishers = publishers?.map { it.name },
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
    @Json(name = "original_url") val originalUrl: String
)

/** Used for the images group for a game details. Does not contain all fields as [NetworkImage] */
@JsonClass(generateAdapter = true)
data class NetworkImageSimple(
    @Json(name = "icon_url") val iconUrl: String?,
    @Json(name = "medium_url") val mediumUrl: String?,
    @Json(name = "screen_url") val screenUrl: String?,
    @Json(name = "small_url") val smallUrl: String?,
    @Json(name = "super_url") val superUrl: String?,
    @Json(name = "thumb_url") val thumbUrl: String?,
    @Json(name = "tiny_url") val tinyUrl: String?,
    @Json(name = "original") val originalUrl: String
)

@JsonClass(generateAdapter = true)
data class NetworkPlatform(
    val name: String,
    val abbreviation: String
)
