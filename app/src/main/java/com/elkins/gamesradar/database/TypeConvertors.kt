package com.elkins.gamesradar.database

import androidx.room.TypeConverter
import java.lang.StringBuilder

class TypeConvertors {

    /* Created for "Platforms", will be utilized in the future */
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val sb: StringBuilder = StringBuilder()
        for(s in value) {
            sb.append(s)
        }
        return sb.toString()
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val strings = mutableListOf<String>()
        for(s in value.split(",")) {
            strings.add(s)
        }
        return strings
    }
}