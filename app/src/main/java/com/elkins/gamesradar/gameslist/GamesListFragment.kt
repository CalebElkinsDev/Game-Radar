package com.elkins.gamesradar.gameslist

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGamesListBinding
import com.elkins.gamesradar.repository.DatabaseFilter
import com.elkins.gamesradar.repository.GamesRepository
import com.elkins.gamesradar.repository.getDatabaseFilterEndDate
import com.elkins.gamesradar.repository.getDatabaseFilterStartDate


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

        initializeRecyclerView()

        // Initialize ViewModel
        val viewModelFactory = GamesListViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesListViewModel::class.java)

        viewModel.games.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // Setup buttons for modifying filter: TODO remove after testing
        binding.upcomingGames.setOnClickListener { updateFilterTest(GamesRepository.ReleaseWindow.UPCOMING) }
        binding.pastMonth.setOnClickListener { updateFilterTest(GamesRepository.ReleaseWindow.PAST_MONTH) }
        binding.pastYear.setOnClickListener { updateFilterTest(GamesRepository.ReleaseWindow.PAST_YEAR) }

        return binding.root
    }

    /** Helper function for setting up the recycler view that holds game items */
    private fun initializeRecyclerView() {
        adapter = GamesListRecyclerViewAdapter(ClickListener {
            viewModel.getGameById(it.guid) // TODO Navigate to the game
        })
        binding.list.adapter = adapter
        binding.list.layoutManager = GridLayoutManager(context, columnCount)
    }

    private fun updateFilterTest(releaseWindow: GamesRepository.ReleaseWindow) {
        val databaseFilter = DatabaseFilter(
            getDatabaseFilterStartDate(releaseWindow),
            getDatabaseFilterEndDate(releaseWindow)
        )
        viewModel.updateFilter(databaseFilter)

        object : CountDownTimer(100, 100) {
            override fun onTick(millisUntilFinished: Long) { }

            override fun onFinish() {
                binding.list.scrollToPosition(8)
                binding.list.smoothScrollToPosition(0)
            }
        }.start()
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