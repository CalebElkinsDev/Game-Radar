package com.elkins.gamesradar.gamedetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Class used to contain image links for image galleries in Details and Gallery fragment.
 * Initially created to allow multiple media types(Image or Video), but GiamtBomb API limitations
 * have restricted usage to simple image URLs for now.
 */
@Parcelize
data class GalleryItem(val imageUrl: String) : Parcelable