package com.elkins.gamesradar.gallery


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.elkins.gamesradar.R
import com.elkins.gamesradar.databinding.FragmentGalleryBinding
import com.elkins.gamesradar.gamedetails.GalleryItem
import com.elkins.gamesradar.utility.setSupportBarTitle


/**
 * Fragment for displaying fullscreen versions of images from a
 * [com.elkins.gamesradar.gamedetails.GameDetails] object
 */
class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var galleryItems: List<GalleryItem>
    private var startingPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get arguments from the navigation action
        val args: GalleryFragmentArgs by navArgs()
        galleryItems = args.galleryItems.toList()
        startingPosition = args.startingPosition
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setSupportBarTitle(requireActivity(), getString(R.string.gallery_appbar_title))

        // Create, populate, and assign the adapter to the view pager
        val adapter = GalleryFullscreenAdapter(galleryItems)
        binding.imagesViewPager.adapter = adapter

        // Move to the position of the item clicked on in the details page
        binding.imagesViewPager.setCurrentItem(startingPosition, false)

        return binding.root
    }
}