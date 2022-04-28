package com.elkins.gamesradar.gamedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.elkins.gamesradar.databinding.GalleryItemBinding


class DetailsGalleryRecyclerViewAdapter(private val detailsListener: ClickListener):
    ListAdapter<GalleryItem, DetailsGalleryRecyclerViewAdapter.GalleryViewHolder>(GalleryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GalleryViewHolder {
        return GalleryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        // Bind callback for navigating to game details
        holder.itemView.setOnClickListener {
            detailsListener.onClick(item)
        }
    }

    class GalleryViewHolder(private val binding: GalleryItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GalleryItem) {
            binding.galleryItem = item
        }

        companion object {

            fun from(parent: ViewGroup): GalleryViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = GalleryItemBinding.inflate(inflater, parent, false)
                return GalleryViewHolder(binding)
            }
        }
    }

    /** ItemCallback for list sorting in the recycler view */
    class GalleryDiffCallback : DiffUtil.ItemCallback<GalleryItem>() {
        override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }
    }
}

/** Simple callback class used for handling clicks on items in the list. */
class ClickListener(val clickListener: (game: GalleryItem) -> Unit) {
    fun onClick(item: GalleryItem) = clickListener(item)
}