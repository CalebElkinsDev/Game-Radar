package com.elkins.gamesradar.gameslist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var viewModel: GamesListViewModel
    private lateinit var adapter: GamesListRecyclerViewAdapter

    private var columnCount = 2

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
        }

        // Initialize ViewModel
        val viewModelFactory = GamesListViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesListViewModel::class.java)

        viewModel.games.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return view
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