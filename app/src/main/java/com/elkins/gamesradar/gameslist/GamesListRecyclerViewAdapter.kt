package com.elkins.gamesradar.gameslist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.elkins.gamesradar.R
import com.elkins.gamesradar.database.DatabaseGame

import com.elkins.gamesradar.databinding.GameListItemBinding

/**
 * [RecyclerView.Adapter] that displays [DatabaseGame] objects.
 */
class GamesListRecyclerViewAdapter: ListAdapter<DatabaseGame,
        GamesListRecyclerViewAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GameViewHolder {
        return GameViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class GameViewHolder(private val binding: GameListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DatabaseGame) {
            binding.game = item

            /* Add a custom text view to the platforms group for each platform of the game*/
            binding.platformsGroup.removeAllViews() // Clear previous platforms
            for(platform in item.platforms!!) {
                val view = LayoutInflater.from(binding.root.context).inflate(
                    R.layout.platform_item,
                    binding.platformsGroup,
                    false
                ) as TextView

                view.text = platform
                binding.platformsGroup.addView(view)
            }
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


    class GameDiffCallback : DiffUtil.ItemCallback<DatabaseGame>() {
        override fun areItemsTheSame(oldItem: DatabaseGame, newItem: DatabaseGame): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DatabaseGame, newItem: DatabaseGame): Boolean {
            return oldItem.id == newItem.id
        }
    }
}