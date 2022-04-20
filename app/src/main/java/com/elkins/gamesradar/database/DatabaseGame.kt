package com.elkins.gamesradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseGame (
    @PrimaryKey val id: Long,
    val guid: String,
    val name: String,
    val imageUrl: String?,
    val platforms: List<String>?
    // TODO add release date
)