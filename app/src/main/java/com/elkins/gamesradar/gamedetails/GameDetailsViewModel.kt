package com.elkins.gamesradar.gamedetails

import androidx.lifecycle.*
import com.elkins.gamesradar.GamesRadarApp
import kotlinx.coroutines.launch


/** ViewModel used to persist the details of a game through lifecycle events. */
class GameDetailsViewModel: ViewModel() {

    private val gamesRepository = GamesRadarApp.repository

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