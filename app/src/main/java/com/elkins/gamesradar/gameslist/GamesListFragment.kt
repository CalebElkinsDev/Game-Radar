package com.elkins.gamesradar.gameslist

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGamesListBinding
import com.elkins.gamesradar.utility.hideKeyboard
import com.elkins.gamesradar.utility.setSupportBarTitle
import kotlin.math.min


/**
 * A fragment representing a list of games from the repository based on filters from preferences.
 */
class GamesListFragment : Fragment() {

    private lateinit var binding: FragmentGamesListBinding
    private lateinit var viewModel: GamesListViewModel
    private lateinit var adapter: GamesListRecyclerViewAdapter
    private lateinit var gameSearchView: SearchView

    private val columnCount = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_games_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        initializeRecyclerView()

        // Initialize ViewModel
        val viewModelFactory = GamesListViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesListViewModel::class.java)

        viewModel.games.observe(viewLifecycleOwner) {
            adapter.submitList(it) // Update the recycler view when the data changes

            // Show or hide the "No Results" message
            binding.noResultsTextView.visibility = when(it.isEmpty()) {
                true -> View.VISIBLE
                false -> View.GONE
            }
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

        // Scroll to start of list and handle live data event
        viewModel.scrollToStartEvent.observe(viewLifecycleOwner) {
            if(it) {
                scrollToStart()
                viewModel.handleScrollToStartEvent()
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
        initializeSearchBar(menu)
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

    /** Setup the functionality for the games search bar in the app bar. */
    private fun initializeSearchBar(menu: Menu) {
        gameSearchView = menu.findItem(R.id.gameSearchView).actionView as SearchView

        with(gameSearchView) {
            // Update the database filter whenever text is changed or submitted
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(text: String?): Boolean {
                    submitTextToFilter(text)
                    hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    submitTextToFilter(text)
                    return false
                }
            })

            // Hide the keyboard when the search view loses focus
            setOnFocusChangeListener { _, focused ->
                if(!focused) { hideKeyboard() }
            }

            // If there is already a name filter, expand the search view and show it
            if(!viewModel.getFilterName().isNullOrBlank()) {
                setQuery(viewModel.getFilterName(), false)
                isIconified = false
                clearFocus()
            }
        }
    }

    /** Update the current database filter with the text as the "name" paramter. */
    private fun submitTextToFilter(text: String?) {
        viewModel.updateFilterName(text)
    }

    /** Called by the menu icon to open the settings fragment */
    private fun navigateToSettings() {
        findNavController().navigate(GamesListFragmentDirections
            .actionGamesListFragmentToSettingsFragment())
        hideKeyboard()
    }

    /** Perform cleanup during onDestroy. */
    override fun onDestroy() {
        super.onDestroy()
        if(::viewModel.isInitialized) {
            submitTextToFilter("") // Reset the name filter
        }
        hideKeyboard() // Hide keyboard in case it was still showing for search bar
    }

    /** Scroll near the beginning of the list and then smooth scroll to first position. */
    private fun scrollToStart() {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            binding.list.scrollToPosition(min(8, adapter.currentList.size))
        }, 100)

        handler.postDelayed({
            binding.list.smoothScrollToPosition(0)
        }, 200)
    }
}