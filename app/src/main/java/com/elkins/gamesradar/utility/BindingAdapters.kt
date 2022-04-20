package com.elkins.gamesradar.utility

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
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
fun TextView.dateFromMillis(timeInMills: Long?) {
    val calender = Calendar.getInstance()
    calender.timeInMillis = timeInMills?: Long.MAX_VALUE

    text = originalReleaseDateFormat.format(calender.time)
}