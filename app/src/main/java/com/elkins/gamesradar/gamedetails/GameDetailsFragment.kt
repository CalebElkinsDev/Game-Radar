package com.elkins.gamesradar.gamedetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.elkins.gamesradar.R
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
}