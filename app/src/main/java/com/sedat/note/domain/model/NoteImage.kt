package com.sedat.note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "T_NoteImage")
data class NoteImage(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var rootID: Int,
    var imageFileUrl: String,
    var description: String
)
