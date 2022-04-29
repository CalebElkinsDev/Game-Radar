package com.elkins.gamesradar.gameslist

import android.app.Application
import androidx.lifecycle.*
import com.elkins.gamesradar.database.getDatabase
import com.elkins.gamesradar.repository.GamesRepository
import com.elkins.gamesradar.repository.getDatabaseFilterEndDate
import com.elkins.gamesradar.repository.getDatabaseFilterStartDate
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
    }

    /** Update the sort order of the database filter */
    fun updateFilterSortOrder(newOrder: Boolean) {
        val orderString = if(newOrder) "asc" else "desc"
        gamesRepository.databaseFilter.value = gamesRepository.databaseFilter.value.also {
            it?.sortOrder = orderString
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