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
class GamesListRecyclerViewAdapter(private val detailsListener: ClickListener,
                                   private val followingListener: FollowingListener):
    ListAdapter<DatabaseGame, GamesListRecyclerViewAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GameViewHolder {
        return GameViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, followingListener)

        // Bind callback for navigating to game details
        holder.itemView.setOnClickListener {
            detailsListener.onClick(item)
        }
    }


    class GameViewHolder(private val binding: GameListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DatabaseGame, followingListener: FollowingListener) {
            binding.game = item

            // Handle on click of toggle group for following status
            binding.toggleLayout.setOnClickListener {
                // Toggle the toggle button when the surrounding layout is clicked
                val newStatus = !binding.followingToggleButton.isChecked
                binding.followingToggleButton.isChecked = newStatus

                // Pass along the new status to the GamesListFragment
                followingListener.onClick(newStatus)
            }

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


    /** ItemCallback for list sorting in the recycler view */
    class GameDiffCallback : DiffUtil.ItemCallback<DatabaseGame>() {
        override fun areItemsTheSame(oldItem: DatabaseGame, newItem: DatabaseGame): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DatabaseGame, newItem: DatabaseGame): Boolean {
            return oldItem.releaseDateInMillis == newItem.releaseDateInMillis
        }
    }
}

/** Simple callback class used for handling clicks on items in the list. */
class ClickListener(val clickListener: (game: DatabaseGame) -> Unit) {
    fun onClick(game: DatabaseGame) = clickListener(game)
}

/** Callback for returning a boolean. Used to change "following" status of game. */
class FollowingListener(val followingListener: (following: Boolean) -> Unit) {
    fun onClick(following: Boolean) = followingListener(following)
}