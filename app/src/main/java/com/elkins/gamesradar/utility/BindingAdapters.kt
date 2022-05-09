package com.elkins.gamesradar.utility

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.elkins.gamesradar.R
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.gamedetails.GameDetails
import com.elkins.gamesradar.utility.NetworkObjectConstants.Companion.DATE_UNKNOWN
import java.util.*


// Use Glide to load a game's image from url
@BindingAdapter("thumbnail")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_image_placeholder_24)
                .error(R.drawable.ic_baseline_error_outline_24))
            .into(view)
    }
}

// Use Glide to load a game's full image from url
@BindingAdapter("galleryImage")
fun fetchGalleryImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_image_placeholder_light)
                .error(R.drawable.ic_baseline_error_outline_light))
            .into(view)
    }
}

/** Convert a list of Strings to a single comma separated string */
@BindingAdapter("displayList")
fun TextView.listToString(list: List<String>?) {
    var listString = ""

    list?.let {
        listString = list.joinToString {
            it
        }
    }
    text = listString
}

@BindingAdapter("releaseDate")
fun TextView.displayReleaseDate(game: DatabaseGame) {

    game.run {
        if(originalReleaseDate > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = game.originalReleaseDate

            text = originalReleaseDateDisplayFormat.format(calendar.time)

        } else if(expectedReleaseYear > 0 && expectedReleaseMonth > 0 && expectedReleaseDay > 0) {

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, expectedReleaseYear)
            calendar.set(Calendar.MONTH, expectedReleaseMonth-1)
            calendar.set(Calendar.DAY_OF_MONTH, expectedReleaseDay)

            text = originalReleaseDateDisplayFormat.format(calendar.time)

        } else if (expectedReleaseMonth > 0 && expectedReleaseYear > 0) {

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, expectedReleaseYear)
            calendar.set(Calendar.MONTH, expectedReleaseMonth-1)
            calendar.set(Calendar.DAY_OF_MONTH, 28)

            val release = "${monthOnlyDateFormat.format(calendar.time)}, $expectedReleaseYear"
            text = release

        } else if(expectedReleaseQuarter > 0) {
            val release = "Q$expectedReleaseQuarter, $expectedReleaseYear"
            text = release

        } else {
            text = DATE_UNKNOWN
        }
    }
}

// Overloaded method used for GameDetails objects in the details fragment
@BindingAdapter("releaseDate")
fun TextView.displayReleaseDate(game: GameDetails?) {

    text = ""

    game?.run {
        if(originalReleaseDate > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = game.originalReleaseDate

            text = originalReleaseDateDisplayFormat.format(calendar.time)

        } else if(expectedReleaseYear > 0 && expectedReleaseMonth > 0 && expectedReleaseDay > 0) {

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, expectedReleaseYear)
            calendar.set(Calendar.MONTH, expectedReleaseMonth)
            calendar.set(Calendar.DAY_OF_MONTH, expectedReleaseDay)

            text = originalReleaseDateDisplayFormat.format(calendar.time)

        } else if (expectedReleaseMonth > 0 && expectedReleaseYear > 0) {

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, expectedReleaseMonth)

            val release = "${monthOnlyDateFormat.format(calendar.time)}, $expectedReleaseYear"
            text = release

        } else if(expectedReleaseQuarter > 0) {
            val release = "Q$expectedReleaseQuarter, $expectedReleaseYear"
            text = release

        } else {
            text = DATE_UNKNOWN
        }
    }
}