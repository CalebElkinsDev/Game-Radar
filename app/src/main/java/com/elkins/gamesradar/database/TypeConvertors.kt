package com.elkins.gamesradar.database

import androidx.room.TypeConverter
import java.lang.StringBuilder
import java.util.*

class TypeConvertors {

    /* Type convertors for "Platforms" */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if(value != null) {
            val sb = StringBuilder()
            for(s in value) {
                sb.append(s).append(",")
            }
            return sb.toString().removeSuffix(",")
        } else {
            return ""
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if(value != null) {
            val strings = mutableListOf<String>()
            for (s in value.split(",")) {
                strings.add(s)
            }
            return strings
        } else {
            return null
        }
    }
}