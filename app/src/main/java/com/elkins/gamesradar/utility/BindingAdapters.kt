package com.elkins.gamesradar.utility

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


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
