package com.elkins.gamesradar.gameslist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGamesListBinding
import com.elkins.gamesradar.network.GiantBombApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class GamesListFragment : Fragment() {

    private lateinit var binding: FragmentGamesListBinding
    private lateinit var adapter: GamesListRecyclerViewAdapter

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = GridLayoutManager(context, columnCount)
            adapter = GamesListRecyclerViewAdapter()
            view.adapter = adapter

//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter = GamesListRecyclerViewAdapter()
//
//            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO Remove api test code
        GlobalScope.launch {
            Log.d("Network", "Fetching Giant Bomb games")
            val response = GiantBombApi.retrofitService.getAllGames(
                apikey = "66e90279e18122006ea7d509821c519bb14bfe1d")

            for(game in response.body()?.results!!) {
                Log.d("Game", game.image!!.originalUrl!!)
            }
        }

    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            GamesListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}