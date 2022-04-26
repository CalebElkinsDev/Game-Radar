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
        val viewModelFactory = GamesDetailsViewModelFactory(requireActivity().application, guid)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(GameDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Set details in binding layout after the view model has fetched the details from the API
//        viewModel.gameDetails.observe(viewLifecycleOwner) {
//            if(it != null) {
//                initializePlatforms()
//            }
//        }
//    }
//
//    /** Populate the platforms. Used after the game details are fetched */
//    private fun initializePlatforms() {
//        /* Add a custom text view to the platforms group for each platform of the game*/
//
//        for(platform in viewModel.gameDetails.value!!.platforms!!) {
//            val view = LayoutInflater.from(binding.root.context).inflate(
//                R.layout.platform_item,
//                binding.platformsGroup,
//                false
//            ) as TextView
//
//            view.text = platform
//            binding.platformsGroup.addView(view)
//        }
//        binding.executePendingBindings()
//    }
}