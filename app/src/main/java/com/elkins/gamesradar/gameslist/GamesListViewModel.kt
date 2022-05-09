package com.elkins.gamesradar.gameslist

import android.util.Log
import androidx.lifecycle.*
import com.elkins.gamesradar.GamesRadarApp
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.repository.GamesRepository
import com.elkins.gamesradar.repository.getDatabaseFilterEndDate
import com.elkins.gamesradar.repository.getDatabaseFilterStartDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class GamesListViewModel : ViewModel() {

    private val gamesRepository = GamesRadarApp.repository

    val games = Transformations.switchMap(gamesRepository.databaseFilter) {
        gamesRepository.getGames(it)
    }

    private val _gameToNavigateTo = MutableLiveData<String?>()
    val gameToNavigateTo: LiveData<String?>
        get() = _gameToNavigateTo

    private val _scrollToStartEvent = MutableLiveData(false)
    val scrollToStartEvent: LiveData<Boolean>
        get() = _scrollToStartEvent

    val databaseProgress = gamesRepository.databaseProgress

    init {
        // Download the games database if it is empty(e.g., first use of application)
        GlobalScope.launch {
            val databaseSize = gamesRepository.getDatabaseSize()
            if(databaseSize <= 0) {
                try {
                    gamesRepository.getGamesFromNetwork()
                } catch (networkError: IOException) {
                    Log.e(
                        "Network Error", networkError.message ?: "Network error " +
                        "in GamesListViewModel")
                    // TODO Handle error in Fragment
                }
            }
        }
    }

    /** Update the platforms to filer the repository by */
    fun updateFilterPlatforms(platforms: List<String>) {
        gamesRepository.databaseFilter.value = gamesRepository.databaseFilter.value.also {
            it?.platforms = platforms
        }
    }

    /** Update the release dates to filter the repository by based on the ReleaseWindow */
    fun updateFilterReleaseDates(releaseWindow: GamesRepository.ReleaseWindow) {
        gamesRepository.databaseFilter.value = gamesRepository.databaseFilter.value.also {
            it?.startDate = getDatabaseFilterStartDate(releaseWindow)
            it?.endDate = getDatabaseFilterEndDate(releaseWindow)
        }
        startScrollToStartEvent() // Notify fragment to scroll to starting position
    }

    /** Update the name filter to find games containing the supplied text. Searches all when blank */
    fun updateFilterName(text: String?) {
        gamesRepository.databaseFilter.value = gamesRepository.databaseFilter.value.also {
            it?.name = text
        }
    }

    /** Get the current value of the database filter's "name" field. */
    fun getFilterName(): String? {
        return gamesRepository.databaseFilter.value?.name
    }

    /** Update the sort order of the database filter */
    fun updateFilterSortOrder(newOrder: Boolean) {
        val orderString = if(newOrder) "asc" else "desc"
        gamesRepository.databaseFilter.value = gamesRepository.databaseFilter.value.also {
            it?.sortOrder = orderString
        }
    }

    /** Call the repository's method for updating a game's "following" status */
    suspend fun updateFollowing(game: DatabaseGame) {
        withContext(Dispatchers.IO) {
            gamesRepository.updateFollowing(game)
        }
    }

    fun startNavigateToDetailsPage(guid: String) {
        _gameToNavigateTo.value = guid
    }

    fun navigateToDetailsPageHandled() {
        _gameToNavigateTo.value = null
    }

    private fun startScrollToStartEvent() {
        _scrollToStartEvent.value = true
    }

    fun handleScrollToStartEvent() {
        _scrollToStartEvent.value = false
    }
}