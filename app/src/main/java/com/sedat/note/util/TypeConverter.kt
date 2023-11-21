package com.sedat.note.util

import com.google.gson.Gson

class TypeConverter {

    fun toString(list: IntArray): String{
        return Gson().toJson(list)
    }

    fun fromString(list: String): IntArray{
        return Gson().fromJson(list, IntArray::class.java)
    }
}