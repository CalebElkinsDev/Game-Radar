package com.elkins.gamesradar.utility

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.elkins.gamesradar.database.DatabaseGame
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*


// Use Glide to load a movie's poster as a thumbnail for the movie list
@BindingAdapter("thumbnail")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
//            .apply( // TODO Add placeholder and error images
//                RequestOptions()
//                .placeholder()
//                .error())
            .thumbnail(0.1f)
            .into(view)
    }
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
            calendar.set(Calendar.MONTH, expectedReleaseMonth)
            calendar.set(Calendar.DAY_OF_MONTH, expectedReleaseDay)

            text = originalReleaseDateDisplayFormat.format(calendar.time)

        } else if (expectedReleaseMonth > 0 && expectedReleaseYear > 0) {

            val calendar = Calendar.getInstance()
            val monthDate = SimpleDateFormat("MMMM")
            calendar.set(Calendar.MONTH, expectedReleaseMonth)

            val release = "${monthDate.format(calendar.time)}, $expectedReleaseYear"
            text = release

        } else if(expectedReleaseQuarter > 0) {
            val release = "Q$expectedReleaseQuarter, $expectedReleaseYear"
            text = release

        } else {
            text = "TBA"
        }
    }

}