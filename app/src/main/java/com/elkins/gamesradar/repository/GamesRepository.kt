package com.elkins.gamesradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.GamesDatabase
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GamesRepository(private val database: GamesDatabase) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val games: LiveData<List<DatabaseGame>> = database.gamesDao.getGames()

    suspend fun getGames() {
        withContext(Dispatchers.IO) {
            database.gamesDao.clearDatabase() // Clear database for testing

            val response = GiantBombApi.retrofitService.getAllGames(
                apikey = "66e90279e18122006ea7d509821c519bb14bfe1d",
                filter = testFilter())
            if(response.body() != null) {
                //Insert into database
                database.gamesDao.insertAll(response.body()!!.results.map {
                    it.asDatabaseModel()
                })
            }
            Log.d("Response Body", response.body().toString())
        }
    }

    private fun testFilter(): String {
        return filterReleaseDates()
    }


    private fun filterReleaseDates(): String {

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val startingReleaseDate = dateFormat.format(calendar.time)

        calendar.add(Calendar.YEAR, Calendar.YEAR+1)
        val endingReleaseDate = dateFormat.format(calendar.time)

        return "original_release_date:" + "${startingReleaseDate}|${endingReleaseDate}"
    }
}