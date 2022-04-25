package com.elkins.gamesradar.gamedetails


/** Domain model of NetworkGameDetail retrieved from single game API calls */
data class GameDetails(
    val id: Long,
    val guid: String,
    val name: String,
    val platforms: List<String>?,
    val imageUrl: String?,
    val images: List<String>?,
    val originalReleaseDate: Long,
    val expectedReleaseYear: Int,
    val expectedReleaseQuarter: Int,
    val expectedReleaseMonth: Int,
    val expectedReleaseDay: Int,
    val description: String? = "N/A",
    val genres: List<String>? = listOf("N/A"),
    val publishers: List<String>? = listOf("N/A")
)