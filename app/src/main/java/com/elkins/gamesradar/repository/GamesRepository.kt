package com.elkins.gamesradar.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import androidx.sqlite.db.SimpleSQLiteQuery
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.GamesDatabase
import com.elkins.gamesradar.database.getDatabase
import com.elkins.gamesradar.gamedetails.GameDetails
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.asDatabaseModel
import com.elkins.gamesradar.network.asDomainModel
import com.elkins.gamesradar.utility.DatabaseConstants
import com.elkins.gamesradar.utility.NetworkObjectConstants
import com.elkins.gamesradar.utility.NetworkObjectConstants.Companion.PLATFORM_FILTER
import com.elkins.gamesradar.utility.PreferenceConstants.Companion.PREF_PLATFORMS
import com.elkins.gamesradar.utility.PreferenceConstants.Companion.PREF_RELEASE_WINDOW
import com.elkins.gamesradar.utility.PreferenceConstants.Companion.PREF_SORT_ORDER
import com.elkins.gamesradar.utility.originalReleaseDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class GamesRepository(private val application: Application) {

    enum class ReleaseWindow {
        UPCOMING, PAST_MONTH, PAST_YEAR
    }

    private val database: GamesDatabase = getDatabase(application)
    private val apikey = "66e90279e18122006ea7d509821c519bb14bfe1d" // TODO REMOVE

    /** The LiveData that is observed for getting filtered results from the database*/
    var databaseFilter: MutableLiveData<DatabaseFilter> = initializeDatabaseFilterFromPrefs()

    fun clearDatabase() {
        database.gamesDao.clearDatabase()
    }

    private var _databaseProgress = MutableLiveData(-1)
    val databaseProgress: LiveData<Int>
        get() = _databaseProgress

    /** WIP: Will be used to get all games within the collective timeframes the app utilizes */
    suspend fun getGamesFromNetwork() {

        withContext(Dispatchers.IO) {

            var totalGamesToAdd = -1
            var totalGamesAdded = 0
            val filter = getAllGamesNetworkFilter()

            do {
                val response = GiantBombApi.retrofitService.getAllGames(
                    apikey = apikey,
                    offset = totalGamesAdded,
                    filter = filter
                )

                if (response.body() != null) {

                    // Determine how many games to add during the first loop
                    if (totalGamesToAdd == -1) {
                        totalGamesToAdd = response.body()!!.totalResults
                    }

                    // Map the fetched NetworkGames to a list of DatabaseGames
                    val fetchedGames = response.body()!!.results.map {
                        it.asDatabaseModel()
                    }
                    // Attempt to insert or update each game
                    for(game in fetchedGames) {
                        database.gamesDao.insertOrUpdateGame(game)
                    }

                    totalGamesAdded += response.body()!!.results.size

                    // Update the progress live data
                    val progress: Double = (totalGamesAdded / totalGamesToAdd.toDouble()) * 100
                    _databaseProgress.postValue(progress.toInt())

                    Log.d(
                        "Response Body", "Offset: ${response.body()!!.offset}" +
                                ", Total Games: ${response.body()!!.totalResults}" +
                                ", GamesAdded: $totalGamesAdded"
                    )
                } else {
                    Log.d("Repository", "Response body null")
                }

                // Delay for 1 second between API calls per API request limitations
                Thread.sleep(1050)

            } while(totalGamesAdded < totalGamesToAdd)

            // Download finished
            _databaseProgress.postValue(100)
            Log.d("Network", "Finished downloding database")
        }
    }

    fun getDatabaseSize(): Long {
        val databaseSize = database.gamesDao.getDatabaseSize()
        Log.d("Database", "Games in db: $databaseSize")
        return databaseSize
    }

    /** Return a [GameDetails] object from the API based on the guid passed to the call. */
    suspend fun fetchGameById(guid: String): GameDetails? {
        var details: GameDetails? = null
        try {
            val response = GiantBombApi.retrofitService.getGameById(guid = guid, apikey = apikey)

            if(response.body() != null) {
                details = response.body()!!.results.asDomainModel()
            } else {
                Log.d("Network Error", "Response body empty")
            }
        } catch (e: Exception) {
            Log.e("Network Error", e.printStackTrace().toString())
        }

        return details
    }

    /** Get all games in the database that meet the criteria of the [DatabaseFilter] */
    fun getGames(filter: DatabaseFilter): LiveData<List<DatabaseGame>> {
        val query = buildGamesListQuery(filter)
        return database.gamesDao.getGames(query)
    }

    /** Update the "following" status of the supplied game. */
    suspend fun updateFollowing(game: DatabaseGame) {
        database.gamesDao.updateGame(game)
    }

    /**
     * Create a [DatabaseFilter] from the default shared preferences and return it wrapped as
     * MutableLiveData.
     */
    private fun initializeDatabaseFilterFromPrefs(): MutableLiveData<DatabaseFilter> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        // Get the enum value of the saved release window and then the date strings from that
        val releaseWindowString = sharedPreferences.getString(PREF_RELEASE_WINDOW, "UPCOMING")?.uppercase()
        val releaseWindow = ReleaseWindow.valueOf(releaseWindowString?: "UPCOMING")
        val startDate = getDatabaseFilterStartDate(releaseWindow)
        val endDate = getDatabaseFilterEndDate(releaseWindow)

        // Map the Set of platform strings into a List
        val platformsSet = sharedPreferences.getStringSet(PREF_PLATFORMS, emptySet())
        val platforms: List<String> = platformsSet!!.map { it }

        // Convert the Boolean preference into the corresponding SQL statement
        val sortOrder = sharedPreferences.getBoolean(PREF_SORT_ORDER, true)
        val sortString = if(sortOrder) "asc" else "desc"

        // Create a DatabaseFilter from the acquired preferences
        val filter = DatabaseFilter(
            startDate = startDate,
            endDate = endDate,
            platforms = platforms,
            sortOrder = sortString
        )

        return MutableLiveData(filter)
    }

    /**
     * Construct a SQL query based on the user preferences to retrieve filtered results from the
     * database.
     */
    private fun buildGamesListQuery(filter: DatabaseFilter): SimpleSQLiteQuery {
        val querySelect = "SELECT * FROM ${DatabaseConstants.GAMES_TABLE_NAME} "

        val queryReleaseDates = "WHERE ${DatabaseConstants.RELEASE_DATE_IN_MILLIS} > ${filter.startDate} " +
                "AND ${DatabaseConstants.RELEASE_DATE_IN_MILLIS} < ${filter.endDate} "

        val queryPlatforms = databaseFilterPlatforms()

        val queryTitle = when(filter.name.isNullOrEmpty()) {
            true -> "" // Add nothing if no name filter
            false -> "AND name LIKE '%${filter.name}%' "
        }

        val queryOrder = "ORDER BY NOT following, ${DatabaseConstants.RELEASE_DATE_IN_MILLIS} ${filter.sortOrder}"

        return SimpleSQLiteQuery(querySelect + queryReleaseDates + queryPlatforms + queryTitle + queryOrder)
    }

    /** Create and return the complete filter for the "games" endpoint filter param */
    private fun getAllGamesNetworkFilter(): String {
        var filter = getNetworkReleaseDateFilter()
        filter += ",${PLATFORM_FILTER}"
        return filter
    }

    /** Return the filter field for original_release_date */
    private fun getNetworkReleaseDateFilter(): String {
        val calendar: Calendar = Calendar.getInstance()

        /* Get the start time for filter */
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.YEAR, -1)
        val startingReleaseDate = originalReleaseDateFormat.format(calendar.time)

        // Get the end time for the filter
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.YEAR, Calendar.YEAR+10)
        val endingReleaseDate = originalReleaseDateFormat.format(calendar.time)

        return NetworkObjectConstants.ORIGINAL_RELEASE_DATE + ":${startingReleaseDate}|${endingReleaseDate}"
    }

    /** Format SQL filter for currently selected platforms */
    private fun databaseFilterPlatforms(): String {

        // Get the selected platforms from the current filter
        val platforms = databaseFilter.value?.platforms?: emptyList()

        val platformsExpanded: MutableList<String> = platforms.map {
            it
        }.toMutableList()

        // Replace the group tag "MOBILE", with individual elements
        if(platformsExpanded.contains("MOBILE")) {
            platformsExpanded.addAll(listOf("IPHN", "IPAD", "ANDR"))
            platformsExpanded.remove("MOBILE")
        }

        return if (!platformsExpanded.isNullOrEmpty()) {
            // Create a LIKE statement for each platform selected
            platformsExpanded.joinToString(prefix = "AND (", postfix = ")", separator = " OR ") {
                "platforms LIKE '%${it}%'"
            }
        } else {
            // Return nothing in order to retrieve all games if filter is null or empty
            ""
        }
    }
}