package com.elkins.gamesradar.gameslist

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.database.getDatabase
import com.elkins.gamesradar.gamedetails.GameDetails
import com.elkins.gamesradar.repository.DatabaseFilter
import com.elkins.gamesradar.repository.GamesRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

class GamesListViewModel(application: Application) : ViewModel() {

    private val gamesRepository = GamesRepository(getDatabase(application))

    val games = Transformations.switchMap(gamesRepository.databaseFilter) {
        gamesRepository.getGames(it)
    }

    private val _gameToNavigateTo = MutableLiveData<String?>()
    val gameToNavigateTo: LiveData<String?>
        get() = _gameToNavigateTo


    init {
//        // TODO Check if database needs updated beforehand
//        viewModelScope.launch {
//            try {
//                gamesRepository.getGamesFromNetwork()
//            } catch (networkError: IOException) {
//                Log.e("Network Error", networkError.message?: "Network error " +
//                "in GamesListViewModel")
//            }
//        }
    }

    /** Update the repository's database filter options*/
    fun updateFilter(filter: DatabaseFilter) {
        gamesRepository.databaseFilter.value = filter
    }

    fun getGameById(guid: String) {
        viewModelScope.launch {
            try {
                gamesRepository.fetchGameById(guid)
            } catch (e: Exception) {

            }
        }
    }

    fun startNavigateToDetailsPage(guid: String) {
        _gameToNavigateTo.value = guid
    }

    fun navigateToDetailsPageHandled() {
        _gameToNavigateTo.value = null
    }
}

class GamesListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GamesListViewModel(application) as T
    }
}