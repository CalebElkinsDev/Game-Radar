package com.elkins.gamesradar.gamedetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elkins.gamesradar.repository.GamesRepository
import kotlinx.coroutines.launch


class GameDetailsViewModel(application: Application): ViewModel() {

    private val gamesRepository = GamesRepository(application)

    var gameDetails = MutableLiveData<GameDetails?>()

    /** Download the game details and store in the [gameDetails] LiveData*/
    fun fetchGameDetails(guid: String) {
        viewModelScope.launch {
            gameDetails.value = gamesRepository.fetchGameById(guid)
        }
    }

    /**
     * Clear the current game details. Prevents previous data from displaying when navigating
     * to a different game.
     */
    fun clearGameDetails() {
        gameDetails.value = null
    }

}

/** Simple factory for creating a [GameDetailsViewModel] with application and guid arguments */
class GamesDetailsViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameDetailsViewModel(application) as T
    }
}