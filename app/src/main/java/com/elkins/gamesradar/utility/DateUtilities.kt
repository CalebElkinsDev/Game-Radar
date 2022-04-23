package com.elkins.gamesradar.utility

import java.text.SimpleDateFormat
import java.util.*


val originalReleaseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
val originalReleaseDateDisplayFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

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