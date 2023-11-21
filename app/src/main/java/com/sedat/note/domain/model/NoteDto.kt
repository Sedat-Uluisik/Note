package com.sedat.note.domain.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "T_Notes")
data class NoteDto(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var rootID: Int,
    var text: String,
    var time: Long,
    var color: String
){
    fun convertNoteDtoToNote(): Note{
        return Note(id = id, rootID = rootID, text = text, time = time, color = color)
    }
}
