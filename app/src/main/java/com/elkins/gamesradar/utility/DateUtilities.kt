package com.elkins.gamesradar.utility

import android.util.Log
import com.elkins.gamesradar.network.NetworkGame
import java.text.SimpleDateFormat
import java.util.*


val originalReleaseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
val originalReleaseDateDisplayFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
val monthOnlyDateFormat = SimpleDateFormat("MMMM", Locale.US)

class DateUtilities {

    companion object {

        fun networkDateStringToDate(value: String?): Date? {
            var date: Date? = null
            value?.let {
                date = originalReleaseDateFormat.parse(it)
            }
            return date
        }
    }
}

/** Extension function to return a date as a long representing time in milliseconds.
 * [Long.MAX_VALUE] returned when date is null for sorting purposes */
fun Date?.timeInMillis(): Long {

    var timeInMillis = -1L

    if(this != null) {
        val calendar = Calendar.getInstance()
        calendar.time = this
        timeInMillis = calendar.timeInMillis
    }

    return timeInMillis
}

/** Based on the available information from the retrieved game, calculate the release time or the
 * expected release time in milliseconds */
fun NetworkGame.calculateReleaseTimeInMillis(): Long {

    if(this.originalReleaseDate != null) {
        /* Determine if the game has an originalReleaseDate set already. If so, format the date
         * and return the timiInMillis of the Date */
        return try {
            val date = originalReleaseDateFormat.parse(this.originalReleaseDate)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.timeInMillis

        } catch (exception: Exception) {
            Log.d("Date Formatting", exception.toString())
            Long.MAX_VALUE
        }
    }  else if(expectedReleaseYear != null && expectedReleaseMonth != null
        && expectedReleaseDay != null) {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, expectedReleaseYear)
        calendar.set(Calendar.MONTH, expectedReleaseMonth)
        calendar.set(Calendar.DAY_OF_MONTH, expectedReleaseDay)

        return calendar.timeInMillis

    } else if(expectedReleaseMonth != null && expectedReleaseYear != null) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, expectedReleaseMonth)
        calendar.set(Calendar.YEAR, expectedReleaseYear)

        return calendar.timeInMillis

    } else if(expectedReleaseQuarter != null) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, expectedReleaseQuarter * 3)
        calendar.set(Calendar.YEAR, expectedReleaseYear!!)

        return calendar.timeInMillis

    } else {
        return Long.MAX_VALUE // Return max value when no information is available
    }
}