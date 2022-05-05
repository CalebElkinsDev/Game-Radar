package com.elkins.gamesradar.gamedetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGameDetailsBinding
import com.elkins.gamesradar.utility.setSupportBarTitle


class GameDetailsFragment : Fragment() {

    private val galleryColumns = 3

    private lateinit var binding: FragmentGameDetailsBinding
    private lateinit var viewModel: GameDetailsViewModel
    private lateinit var adapter: DetailsGalleryRecyclerViewAdapter
    private lateinit var guid: String
    private lateinit var galleryImages: List<String>

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
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initializeRecyclerView()

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
                galleryImages = getGalleryContents().map { item ->
                    item.imageUrl
                }
                adapter.submitList(getGalleryContents()) // Submit photos/videos to recycler view
                finishLoading()
            } else {
                Log.d("DetailsFragment", "details are null")
            }
        }

        // Observe the viewModel's network error event
        viewModel.networkErrorEvent.observe(viewLifecycleOwner) {
            if(it) {
                networkError()
                viewModel.handleNetworkErrorEvent() // Clear the LiveData event
            }
        }

        // Try again to load the game details after a network error
        binding.reloadNetworkRequestButton.setOnClickListener {
            reloadNetworkRequest()
        }
    }

    /** Helper function for setting up the recycler view that holds teh gallery items */
    private fun initializeRecyclerView() {
        adapter = DetailsGalleryRecyclerViewAdapter(ClickListener {
            //viewModel.startNavigateToDetailsPage(it.guid)
            findNavController().navigate(GameDetailsFragmentDirections
                .actionGameDetailsFragmentToGalleryFragment(galleryImages.toTypedArray()))
            Log.d("Gallery", "Item clicked")
        })
        binding.galleryRecyclerView.adapter = adapter
        binding.galleryRecyclerView.layoutManager = GridLayoutManager(context, galleryColumns)
    }

    /**
     * Get a list of [GalleryItem] from the urls in the "images" value of the current
     * [GameDetails] object.
     */
    private fun getGalleryContents(): List<GalleryItem> {
        var galleryItems: MutableList<GalleryItem>
        viewModel.gameDetails.value.let {
            galleryItems = it?.images?.map { imageUrl ->
                GalleryItem(imageUrl)
            } as MutableList<GalleryItem>
        }
        return galleryItems
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

    /**
     * Called by observer of view model's network error live data event. Stop the progress bar
     * and show an error message to the user about the network error.
     */
    private fun networkError() {
        binding.loadingProgressBar.visibility = View.INVISIBLE
        binding.networkErrorGroup.visibility = View.VISIBLE
    }

    /**
     * Called when the user presses the Reload button. Attempts to load the game details from the
     * API again. Will fail if the user did not connect to the internet before trying again.
     */
    private fun reloadNetworkRequest() {
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.networkErrorGroup.visibility = View.INVISIBLE

        viewModel.fetchGameDetails(guid)
    }
}