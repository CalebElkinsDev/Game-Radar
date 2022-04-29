package com.elkins.gamesradar.database

import androidx.room.TypeConverter
import java.lang.StringBuilder

class TypeConvertors {

    /* Type convertors for "Platforms" */
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if(value != null) {
            val sb = StringBuilder()
            for(s in value) {
                sb.append(s).append(",")
            }
            sb.toString().removeSuffix(",")
        } else {
            ""
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if(value != null) {
            val strings = mutableListOf<String>()
            for (s in value.split(",")) {
                strings.add(s)
            }
            strings
        } else {
            null
        }
    }
}