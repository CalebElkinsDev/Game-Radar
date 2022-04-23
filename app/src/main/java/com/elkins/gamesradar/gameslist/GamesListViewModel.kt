package com.elkins.gamesradar.gameslist

import android.app.Application
import android.graphics.Movie
import android.util.Log
import androidx.lifecycle.*
import com.elkins.gamesradar.database.getDatabase
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.NetworkGame
import com.elkins.gamesradar.repository.GamesRepository
import kotlinx.coroutines.launch
import java.io.IOException

class GamesListViewModel(application: Application) : ViewModel() {

    private val gamesRepository = GamesRepository(getDatabase(application))

    val games = gamesRepository.games


    init {
        // TODO Check if database needs updated beforehand
        viewModelScope.launch {
            try {
                gamesRepository.getGames()
            } catch (networkError: IOException) {
                Log.e("Network Error", networkError.message?: "Network error " +
                "in GamesListViewModel")
            }
        }
    }
}

class GamesListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GamesListViewModel(application) as T
    }
}