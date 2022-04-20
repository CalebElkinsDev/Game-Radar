package com.elkins.gamesradar.repository

import androidx.lifecycle.LiveData
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.GamesDatabase
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GamesRepository(private val database: GamesDatabase) {

    val games: LiveData<List<DatabaseGame>> = database.gamesDao.getGames()

    suspend fun getGames() {
        withContext(Dispatchers.IO) {
            val response = GiantBombApi.retrofitService.getAllGames(apikey = "66e90279e18122006ea7d509821c519bb14bfe1d")
            if(response.body() != null) {
                //Insert into database
                database.gamesDao.insertAll(response.body()!!.results.map {
                    it.asDatabaseModel()
                })
            }
        }
    }
}