package com.elkins.gamesradar.repository

import android.text.format.Time
import android.util.Log
import androidx.lifecycle.LiveData
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.GamesDatabase
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.asDatabaseModel
import com.elkins.gamesradar.utility.originalReleaseDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GamesRepository(private val database: GamesDatabase) {

    val games: LiveData<List<DatabaseGame>> = database.gamesDao.getGames()

    suspend fun getGames() {
        withContext(Dispatchers.IO) {
            database.gamesDao.clearDatabase() // Clear database for testing

            val response = GiantBombApi.retrofitService.getAllGames(
                apikey = "66e90279e18122006ea7d509821c519bb14bfe1d",
                filter = testFilter(),
                sort = getSort())

            if(response.body() != null) {
                //Insert into database
                database.gamesDao.insertAll(response.body()!!.results.map {
                    it.asDatabaseModel()
                })
            }
            Log.d("Response Body", response.body().toString())
        }
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
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.YEAR, -1)
        val startingReleaseDate = originalReleaseDateFormat.format(calendar.time)

        calendar.timeInMillis = System.currentTimeMillis()
        val endingReleaseDate = originalReleaseDateFormat.format(calendar.time)

        return "original_release_date:" + "${startingReleaseDate}|${endingReleaseDate}"
    }
}