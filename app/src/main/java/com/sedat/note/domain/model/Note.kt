package com.sedat.note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "T_Notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var rootID: Int,
    var text: String,
    var time: Long
)
