package com.elkins.gamesradar.gameslist

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGamesListBinding
import com.elkins.gamesradar.utility.setSupportBarTitle
import kotlin.math.min


/**
 * A fragment representing a list of games from the repository based on filters from preferences.
 */
class GamesListFragment : Fragment() {

    private lateinit var binding: FragmentGamesListBinding
    private lateinit var viewModel: GamesListViewModel
    private lateinit var adapter: GamesListRecyclerViewAdapter

    private val columnCount = 2

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
            adapter.submitList(it) // Update the recycler view when the data changes

            // Jump to near the beginning of the list and then smooth scroll to start on changes
            // TODO Trigger this only when filters change, not when a game is favorited
            object : CountDownTimer(100, 100) {
                override fun onTick(millisUntilFinished: Long) { }

                override fun onFinish() {
                    binding.list.scrollToPosition(min(8, it.size))
                    binding.list.smoothScrollToPosition(0)
                }
            }.start()
        }

        viewModel.gameToNavigateTo.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()) {
                Log.d("Navigation", "GUID going to = $it")
                findNavController().navigate(
                    GamesListFragmentDirections.actionGamesListFragmentToGameDetailsFragment(it)
                )
                viewModel.navigateToDetailsPageHandled() // Clear the live data event after handled
            }
        }

        // Enable the app bar menu
        setHasOptionsMenu(true)

        // Set the title for the appbar
        setSupportBarTitle(requireActivity(), getString(R.string.list_appbar_title))

        return binding.root
    }

    /** Inflate and add the games list menu */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.games_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /** Handle user menu selections*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.settingsMenuButton -> {
                navigateToSettings()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** Helper function for setting up the recycler view that holds game items */
    private fun initializeRecyclerView() {
        adapter = GamesListRecyclerViewAdapter(ClickListener {
            viewModel.startNavigateToDetailsPage(it.guid)
        })
        binding.list.adapter = adapter
        binding.list.layoutManager = GridLayoutManager(context, columnCount)
    }

    /** Called by the menu icon to open the settings fragment */
    private fun navigateToSettings() {
        findNavController().navigate(GamesListFragmentDirections
            .actionGamesListFragmentToSettingsFragment())
    }
}