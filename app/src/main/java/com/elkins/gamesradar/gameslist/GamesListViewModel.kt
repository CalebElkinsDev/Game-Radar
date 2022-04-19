package com.elkins.gamesradar.gameslist

import android.graphics.Movie
import androidx.lifecycle.*
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.network.NetworkGame
import kotlinx.coroutines.launch

class GamesListViewModel : ViewModel() {

    private var _games = MutableLiveData<List<NetworkGame>>()
    val games: LiveData<List<NetworkGame>>
        get() = _games

    init {
        viewModelScope.launch {
            val response = GiantBombApi.retrofitService.getAllGames(apikey = "66e90279e18122006ea7d509821c519bb14bfe1d")
            if(response.body() != null) {
                _games.value = response.body()?.results
            }
        }
    }
}

class GamesListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GamesListViewModel() as T
    }
}