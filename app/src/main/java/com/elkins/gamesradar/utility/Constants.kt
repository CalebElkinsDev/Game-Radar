package com.elkins.gamesradar.utility

import com.elkins.gamesradar.network.NetworkPlatform


class NetworkObjectConstants {
    companion object {
        const val ORIGINAL_RELEASE_DATE = "original_release_date"
    }
}

class DatabaseConstants {
    companion object {
        const val GAMES_TABLE_NAME = "databasegame"
        const val RELEASE_DATE_IN_MILLIS = "releaseDateInMillis"
    }
}

/** Data class for getting relevant platforms and their preferred order */
data class Platform(private val name: String, private val abbreviation: String)

/** A List containing all applicable platforms. Entries in order they should appear in lists */
val ALL_PLATFORMS = listOf(
    Platform("PS4", "PlayStation 4"),
    Platform("PS5", "PlayStation 5"),
    Platform("XONE", "Xbox One"),
    Platform("XSX", "Xbox Series X"),
    Platform("NSW", "Nintendo Switch"),
    Platform("IPHN", "iPhone"),
    Platform("IPAD", "iPad"),
    Platform("ANDR", "Android"),
    Platform("PC", "PC"),
    Platform("MAC", "Mac"),
    Platform("STAD", "Stadia"),
    Platform("APTV", "Apple TV"),
    Platform("3DS", "Nintendo 3DS"),
    Platform("3DSE", "3DS eShop"),
    Platform("N3DS", "New 3DS"),
    Platform("ARC", "Arcade"),
    Platform("BROW", "Browser"),
    Platform("NONE", "No platforms")
)