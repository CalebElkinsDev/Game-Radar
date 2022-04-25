package com.elkins.gamesradar.gamedetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elkins.gamesradar.database.getDatabase
import com.elkins.gamesradar.repository.GamesRepository
import kotlinx.coroutines.launch


class GameDetailsViewModel(application: Application, guid: String): ViewModel() {

    private val gamesRepository = GamesRepository(getDatabase(application))

    var gameDetails = MutableLiveData<GameDetails>()

    init {
        // Download the details of the current game
        viewModelScope.launch {
            gameDetails.value = gamesRepository.fetchGameById(guid)
        }
    }

}

/** Simple factory for creating a [GameDetailsViewModel] with application and guid arguments */
class GamesDetailsViewModelFactory(private val application: Application, private val guid: String)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameDetailsViewModel(application, guid) as T
    }
}