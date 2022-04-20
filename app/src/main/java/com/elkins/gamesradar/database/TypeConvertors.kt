package com.elkins.gamesradar.database

import androidx.room.TypeConverter
import java.lang.StringBuilder

class TypeConvertors {

    /* Type convertors for "Platforms" */
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val sb = StringBuilder()
        for(s in value) {
            sb.append(s).append(",")
        }
        return sb.toString().removeSuffix(",")
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