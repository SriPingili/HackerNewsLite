package com.android.hackernewslite.play.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {

    @TypeConverter
    fun fromSource(source: List<Int>?): String {

        if (source == null) return ""

        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(stringOfIntegers: String): List<Int> {
        if(stringOfIntegers.isEmpty()) return ArrayList<Int>()
        val listType: Type = object : TypeToken<ArrayList<Int>>() {}.type
        return Gson().fromJson(stringOfIntegers, listType)
    }
}