package com.elkins.gamesradar.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elkins.gamesradar.databinding.GalleryFullscreenItemBinding
import com.elkins.gamesradar.gallery.GalleryFullscreenAdapter.GalleryFullscreenViewHolder
import com.elkins.gamesradar.gamedetails.GalleryItem

class GalleryFullscreenAdapter(private val items: List<GalleryItem>)
    : RecyclerView.Adapter<GalleryFullscreenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryFullscreenViewHolder {
        return GalleryFullscreenViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GalleryFullscreenViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    class GalleryFullscreenViewHolder(private val binding: GalleryFullscreenItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GalleryItem) {
            binding.galleryItem = item
        }

        companion object {
            fun from(parent: ViewGroup): GalleryFullscreenViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = GalleryFullscreenItemBinding.inflate(inflater, parent, false)
                return GalleryFullscreenViewHolder(binding)
            }
        }
    }
}