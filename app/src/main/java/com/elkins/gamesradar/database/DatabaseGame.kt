package com.elkins.gamesradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

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
    val expectedReleaseDay: Int
)