package com.elkins.gamesradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.GamesDatabase
import com.elkins.gamesradar.gamedetails.GameDetails
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.NetworkGameDetail
import com.elkins.gamesradar.network.asDatabaseModel
import com.elkins.gamesradar.network.asDomainModel
import com.elkins.gamesradar.utility.DatabaseConstants
import com.elkins.gamesradar.utility.NetworkObjectConstants
import com.elkins.gamesradar.utility.originalReleaseDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class GamesRepository(private val database: GamesDatabase) {

    private val apikey = "66e90279e18122006ea7d509821c519bb14bfe1d"

    enum class ReleaseWindow {
        UPCOMING, PAST_MONTH, PAST_YEAR
    }

    private val currentReleaseWindow: ReleaseWindow = ReleaseWindow.UPCOMING

    // TODO save and load filter from prefs
    var databaseFilter: MutableLiveData<DatabaseFilter> = MutableLiveData(DatabaseFilter(
        getDatabaseFilterStartDate(currentReleaseWindow),
        getDatabaseFilterEndDate(currentReleaseWindow)
    ))


    suspend fun getGamesFromNetwork() {

        withContext(Dispatchers.IO) {

            database.gamesDao.clearDatabase() // Clear database for testing

            var totalGamesToAdd = -1
            var totalGamesAdded = 0
            val filter = filterReleaseDates()

            do {
                val response = GiantBombApi.retrofitService.getAllGames(
                    apikey = apikey,
                    offset = totalGamesAdded,
                    filter = filter
                )

                if (response.body() != null) {

                    // Determine how many games to add during the first loop
                    if(totalGamesToAdd == -1) {
                        totalGamesToAdd = response.body()!!.totalResults
                    }

                    //Insert into database
                    database.gamesDao.insertAll(response.body()!!.results.map {
                        it.asDatabaseModel()
                    })

                    totalGamesAdded += response.body()!!.results.size

                    Log.d(
                        "Response Body", "Offset: ${response.body()!!.offset}" +
                                ", Total Games: ${response.body()!!.totalResults}" +
                                ", GamesAdded: $totalGamesAdded"
                    )
                } else {
                    Log.d("Repository", "Response body null")
                    break
                }
            } while(totalGamesAdded < 1000) //totalGamesToAdd)
        }
    }

    suspend fun fetchGameById(guid: String): GameDetails {
        return GiantBombApi.retrofitService.getGameById(guid = guid, apikey = apikey)
            .body()!!.results.asDomainModel()
    }

    /** Get all games in the database that meet the criteria of the [DatabaseFilter] */
    fun getGames(filter: DatabaseFilter): LiveData<List<DatabaseGame>> {

        val query = buildGamesListQuery(filter)

        return database.gamesDao.getGames(query)
    }

    /** Construct a SQL query based on the [DatabaseFilter] to retrieve filtered results from the
     * database. */
    private fun buildGamesListQuery(filter: DatabaseFilter): SimpleSQLiteQuery {
        val querySelect = "SELECT * FROM ${DatabaseConstants.GAMES_TABLE_NAME} "

        val queryWhere = "WHERE ${DatabaseConstants.RELEASE_DATE_IN_MILLIS} > ${filter.startDate} " +
                "AND ${DatabaseConstants.RELEASE_DATE_IN_MILLIS} < ${filter.endDate} "

        val queryOrder = "ORDER BY ${DatabaseConstants.RELEASE_DATE_IN_MILLIS} ${filter.sortOrder}"

        return SimpleSQLiteQuery(querySelect + queryWhere + queryOrder)
    }

    /** Return the filter field for original_release_date */
    private fun filterReleaseDates(): String {

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
}