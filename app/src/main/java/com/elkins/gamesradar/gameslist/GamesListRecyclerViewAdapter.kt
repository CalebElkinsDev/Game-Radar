package com.elkins.gamesradar.gameslist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import com.elkins.gamesradar.gameslist.placeholder.PlaceholderContent.PlaceholderItem
import com.elkins.gamesradar.databinding.GameListItemBinding
import com.elkins.gamesradar.network.NetworkGame

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class GamesListRecyclerViewAdapter(
) : ListAdapter<NetworkGame, GamesListRecyclerViewAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GameViewHolder {
        return GameViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class GameViewHolder(private val binding: GameListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NetworkGame) {
            binding.game = item
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): GameViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = GameListItemBinding.inflate(inflater, parent, false)
                return GameViewHolder(binding)
            }
        }
    }

    class GameDiffCallback : DiffUtil.ItemCallback<NetworkGame>() {
        override fun areItemsTheSame(oldItem: NetworkGame, newItem: NetworkGame): Boolean {
            // Compare the Imdb Id used as the primary key
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NetworkGame, newItem: NetworkGame): Boolean {
            return oldItem.id == newItem.id
        }
    }
}