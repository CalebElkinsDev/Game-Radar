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


class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var galleryItems: List<GalleryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: GalleryFragmentArgs by navArgs()
        galleryItems = args.galleryItems.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setSupportBarTitle(requireActivity(), "Images")

        // Create, populate, and assign the adapter to the view pager
        val adapter = GalleryFullscreenAdapter(galleryItems)
        binding.imagesViewPager.adapter = adapter

        return binding.root
    }
}