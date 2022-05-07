package com.elkins.gamesradar.utility


class NetworkObjectConstants {
    companion object {
        const val ORIGINAL_RELEASE_DATE = "original_release_date"
        const val DATE_UNKNOWN = "TBA"
        const val PLATFORM_FILTER = "platforms:146|176|146|179|157|96|120|123|94|17|175|84|140"
    }
}

class DatabaseConstants {
    companion object {
        const val GAMES_TABLE_NAME = "databasegame"
        const val RELEASE_DATE_IN_MILLIS = "releaseDateInMillis"
    }
}

class PreferenceConstants {
    companion object {
        const val PREF_PLATFORMS = "platforms"
        const val PREF_RELEASE_WINDOW = "releaseWindow"
        const val PREF_SORT_ORDER = "sortOrder"
        const val PREF_CLEAR_CACHE = "clearCache"
        const val CACHE_CLEAR_TIMER = 500L
    }
}

/** Data class for getting relevant platforms and their preferred order */
data class Platform(val name: String, val abbreviation: String, val id: Int)

/** A List containing all applicable platforms. Entries in order they should appear in lists */
val ALL_PLATFORMS = listOf(
    Platform("PS4", "PlayStation 4", 146),
    Platform("PS5", "PlayStation 5", 176),
    Platform("XONE", "Xbox One", 145),
    Platform("XSX", "Xbox Series X", 179),
    Platform("NSW", "Nintendo Switch", 157),
    Platform("IPHN", "iPhone", 96),
    Platform("IPAD", "iPad", 120),
    Platform("ANDR", "Android", 123),
    Platform("PC", "PC", 94),
    Platform("MAC", "Mac", 17),
    Platform("STAD", "Stadia", 175),
    Platform("ARC", "Arcade", 84),
    Platform("BROW", "Browser", 140),
    Platform("NONE", "No platforms", -1)
)