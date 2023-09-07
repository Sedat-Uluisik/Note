package com.sedat.note.domain.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "T_Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var rootID: Int,
    var text: String,
    var time: Long,
){
    fun convertDate(): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return format.format(date).toString()
    }
}
