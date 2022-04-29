package com.elkins.gamesradar.gamedetails

import android.app.Application
import androidx.lifecycle.*
import com.elkins.gamesradar.repository.GamesRepository
import kotlinx.coroutines.launch


class GameDetailsViewModel(application: Application): ViewModel() {

    private val gamesRepository = GamesRepository(application)

    var gameDetails = MutableLiveData<GameDetails?>()

    private var _networkErrorEvent = MutableLiveData(false)
    val networkErrorEvent: LiveData<Boolean>
        get() = _networkErrorEvent

    /** Download the game details and store in the [gameDetails] LiveData*/
    fun fetchGameDetails(guid: String) {
        viewModelScope.launch {
            gameDetails.value = gamesRepository.fetchGameById(guid)
            if(gameDetails.value == null) {
                _networkErrorEvent.value = true
            }
        }
    }

    /**
     * Clear the current game details. Prevents previous data from displaying when navigating
     * to a different game.
     */
    fun clearGameDetails() {
        gameDetails.value = null
    }

    /** Called by obersvers when the [_networkErrorEvent] live data event is handled. */
    fun handleNetworkErrorEvent() {
        _networkErrorEvent.value = false
    }

}

/** Simple factory for creating a [GameDetailsViewModel] with application and guid arguments */
class GamesDetailsViewModelFactory(private val application: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameDetailsViewModel(application) as T
    }
}