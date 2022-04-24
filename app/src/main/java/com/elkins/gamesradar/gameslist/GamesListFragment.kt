package com.elkins.gamesradar.gameslist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGamesListBinding
import com.elkins.gamesradar.network.GiantBombApi
import com.elkins.gamesradar.repository.DatabaseFilter
import com.elkins.gamesradar.repository.GamesRepository
import com.elkins.gamesradar.repository.getDatabaseFilterEndDate
import com.elkins.gamesradar.repository.getDatabaseFilterStartDate
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_games_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = GamesListRecyclerViewAdapter()
        binding.list.adapter = adapter
        binding.list.layoutManager = GridLayoutManager(context, columnCount)


        // Initialize ViewModel
        val viewModelFactory = GamesListViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesListViewModel::class.java)

        viewModel.games.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        // Setup buttons for modifying filter: TODO remove after testing
        binding.upcomingGames.setOnClickListener {
            val currentDateFilter = GamesRepository.DateFilter.UPCOMING
            val newFilter = DatabaseFilter(
                getDatabaseFilterStartDate(currentDateFilter),
                getDatabaseFilterEndDate(currentDateFilter)
            )
            viewModel.updateFilter(newFilter)
            binding.list.scrollToPosition(0)
        }
        binding.pastMonth.setOnClickListener {
            val currentDateFilter = GamesRepository.DateFilter.PAST_MONTH
            val newFilter = DatabaseFilter(
                getDatabaseFilterStartDate(currentDateFilter),
                getDatabaseFilterEndDate(currentDateFilter)
            )
            viewModel.updateFilter(newFilter)
            binding.list.scrollToPosition(0)
        }
        binding.pastYear.setOnClickListener {
            val currentDateFilter = GamesRepository.DateFilter.PAST_YEAR
            val newFilter = DatabaseFilter(
                getDatabaseFilterStartDate(currentDateFilter),
                getDatabaseFilterEndDate(currentDateFilter)
            )
            viewModel.updateFilter(newFilter)
            binding.list.scrollToPosition(0)
        }

        return binding.root
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