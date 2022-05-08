package com.elkins.gamesradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 *  @param releaseDateInMillis: Used for sorting and filtering. Is calculated by available
 *  release date information, such as original release date or expected release date + quarter.
 *
 *  @param following: Will be used to indicate if the user is following this game and it should
 *  have sorting priority.
 */

@Entity
data class DatabaseGame (
    @PrimaryKey val id: Long,
    val guid: String,
    val name: String,
    val imageUrl: String?,
    val platforms: List<String>?,
    val originalReleaseDate: Long,
    val expectedReleaseYear: Int,
    val expectedReleaseQuarter: Int,
    val expectedReleaseMonth: Int,
    val expectedReleaseDay: Int,
    val releaseDateInMillis: Long,
    var following: Boolean = false
)