package com.elkins.gamesradar.gamedetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.elkins.gamesradar.R
import com.elkins.gamesradar.database.DatabaseGame
import com.elkins.gamesradar.databinding.FragmentGameDetailsBinding
import com.elkins.gamesradar.gameslist.GamesListViewModel
import com.elkins.gamesradar.gameslist.GamesListViewModelFactory
import com.elkins.gamesradar.utility.setSupportBarTitle


class GameDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGameDetailsBinding
    private lateinit var viewModel: GameDetailsViewModel
    private lateinit var guid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get guid from nav arguments
        val args: GameDetailsFragmentArgs by navArgs()
        guid = args.guid

        // Initialize ViewModel
        val viewModelFactory = GamesDetailsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(GameDetailsViewModel::class.java)

        // Have the view model fetch the details for the current game
        viewModel.fetchGameDetails(guid)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // Set a placeholder for the app bar title until game details are fetched
        setSupportBarTitle(requireActivity(), getString(R.string.details_title_placeholder))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set details in binding layout after the view model has fetched the details from the API
        viewModel.gameDetails.observe(viewLifecycleOwner) {
            if(it != null) {
                setSupportBarTitle(requireActivity(), it.name) // Update app bar title
                finishLoading()
            }
        }
    }

    /** Clear the [GameDetailsViewModel] current game details when leaving the detail fragment */
    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearGameDetails()
    }

    /**
     * Called when view model successfully retrieves the games details to hide the progress
     * bar and display the details
     */
    private fun finishLoading() {
        binding.loadingProgressBar.visibility = View.INVISIBLE
        binding.detailsScrollView.visibility = View.VISIBLE
    }
}