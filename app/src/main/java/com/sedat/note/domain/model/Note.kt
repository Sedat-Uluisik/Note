package com.sedat.note.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(var id: Int = 0,
                 var rootID: Int = 0,
                 var text: String = "",
                 var time: Long = 0L,
                 val subNoteCount: Int = 0,
                 val imageCount: Int = 0
){
    fun convertDate(): String {
        return try {
            val date = Date(time)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            return format.format(date).toString()
        }catch (e: Exception){
            "--:--"
        }
    }
}
