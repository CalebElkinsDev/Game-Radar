package com.elkins.gamesradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.GamesDatabase
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.asDatabaseModel
import com.elkins.gamesradar.utility.originalReleaseDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class GamesRepository(private val database: GamesDatabase) {

    enum class DateFilter {
        UPCOMING, PAST_MONTH, PAST_YEAR
    }

    private val currentDateFilter: DateFilter = DateFilter.UPCOMING

    // TODO save and load filter from prefs
    var databaseFilter: MutableLiveData<DatabaseFilter> = MutableLiveData(DatabaseFilter(
        getDatabaseFilterStartDate(currentDateFilter),
        getDatabaseFilterEndDate(currentDateFilter)
    ))


    suspend fun getGamesFromNetwork() {

        withContext(Dispatchers.IO) {

            database.gamesDao.clearDatabase() // Clear database for testing

            var totalGamesToAdd = -1
            var totalGamesAdded = 0
            val filter = testFilter()

            do {
                val response = GiantBombApi.retrofitService.getAllGames(
                    apikey = "66e90279e18122006ea7d509821c519bb14bfe1d",
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

    /** Get all games in the database that meet the criteria of the [DatabaseFilter] */
    fun getGames(filter: DatabaseFilter): LiveData<List<DatabaseGame>> {

        val query = buildGamesListQuery(filter)

        return database.gamesDao.getGames(query)
    }

    /** Construct a SQL query based on the [DatabaseFilter] to retrieve filtered results from the
     * database. */
    private fun buildGamesListQuery(filter: DatabaseFilter): SimpleSQLiteQuery {
        val querySelect = "SELECT * FROM databasegame "

        val queryWhere = "WHERE releaseDateInMillis > ${filter.startDate} " +
                "AND releaseDateInMillis < ${filter.endDate} "

        val queryOrder = "ORDER BY releaseDateInMillis ${filter.sortOrder}"

        return SimpleSQLiteQuery(querySelect + queryWhere + queryOrder)
    }

    private fun getSort(): String {
        return filterSortOrder(true)
    }

    private fun testFilter(): String {
        return filterReleaseDates()
    }

    private fun filterSortOrder(sortAscending: Boolean): String {
        return "original_release_date:" + if(sortAscending) "asc" else "desc"
    }

    /** Return the filter field for original_release_date */
    private fun filterReleaseDates(): String {

        val calendar: Calendar = Calendar.getInstance()

        /* Get the start time for filter */
        val startingReleaseDate = when(currentDateFilter) {
            DateFilter.UPCOMING -> {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.add(Calendar.MONTH, -3)
                originalReleaseDateFormat.format(calendar.time)
            }
            DateFilter.PAST_MONTH -> {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.add(Calendar.MONTH, -1)
                originalReleaseDateFormat.format(calendar.time)
            }
            DateFilter.PAST_YEAR -> {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.add(Calendar.YEAR, -1)
                originalReleaseDateFormat.format(calendar.time)
            }
        }

        // Get the end time for the filter
        val endingReleaseDate = when(currentDateFilter) {
            DateFilter.UPCOMING -> {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.add(Calendar.YEAR, Calendar.YEAR+10)
                originalReleaseDateFormat.format(calendar.time)
            }
            DateFilter.PAST_MONTH, DateFilter.PAST_YEAR -> {
                calendar.timeInMillis = System.currentTimeMillis()
                originalReleaseDateFormat.format(calendar.time)
            }
        }

        return "original_release_date:" + "${startingReleaseDate}|${endingReleaseDate}"
    }
}