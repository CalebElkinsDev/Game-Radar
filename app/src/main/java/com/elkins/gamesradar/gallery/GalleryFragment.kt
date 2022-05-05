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
import com.elkins.gamesradar.utility.setSupportBarTitle


class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var galleryImages: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: GalleryFragmentArgs by navArgs()
        galleryImages = args.imageUrls.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setSupportBarTitle(requireActivity(), "Images")

        binding.imageUrls = galleryImages[0] // Test showing first image

        return binding.root
    }
}