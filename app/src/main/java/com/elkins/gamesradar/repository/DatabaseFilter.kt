package com.elkins.gamesradar.repository

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.elkins.gamesradar.utility.originalReleaseDateFormat
import java.util.*

/**
 * Class for determining what games to display in the games list. As changes to its fields are
 * made, the LiveData holding the current game's list will be transformed to reflect changes.
 */
class DatabaseFilter(@Bindable var startDate: String,
                     @Bindable var endDate: String,
                     @Bindable var sortOrder: String = "ASC") : BaseObservable() {
}

/**
 * Get the string value of the starting time for filtering games
 * @param dateFilter: Enum for determining what the start date will be
 */
fun getDatabaseFilterStartDate(dateFilter: GamesRepository.DateFilter): String {

    val calendar: Calendar = Calendar.getInstance()

    /* Get the start time for filter in milliseconds */
    val startingReleaseDate = when(dateFilter) {
        GamesRepository.DateFilter.UPCOMING -> {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MONTH, -3)
            calendar.timeInMillis
        }
        GamesRepository.DateFilter.PAST_MONTH -> {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MONTH, -1)
            calendar.timeInMillis
        }
        GamesRepository.DateFilter.PAST_YEAR -> {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.YEAR, -1)
            calendar.timeInMillis
        }
    }

    return startingReleaseDate.toString()
}

/**
 * Get the string value of the ending time for filtering games
 * @param dateFilter: Enum for determining what the end date will be
 */
fun getDatabaseFilterEndDate(dateFilter: GamesRepository.DateFilter): String {

    val calendar: Calendar = Calendar.getInstance()

    // Get the end time for the filter in milliseconds
    val endingReleaseDate = when(dateFilter) {
        GamesRepository.DateFilter.UPCOMING -> {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.YEAR, Calendar.YEAR+10)
            calendar.timeInMillis
        }
        GamesRepository.DateFilter.PAST_MONTH, GamesRepository.DateFilter.PAST_YEAR -> {
            calendar.timeInMillis = System.currentTimeMillis()
            originalReleaseDateFormat.format(calendar.time)
            calendar.timeInMillis
        }
    }

    return endingReleaseDate.toString()
}