package com.sedat.note.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithSubNoteInfo(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "rootID"
    )
    val subNoteList: List<Relationships>
)
