package com.sedat.note.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithImages(
    @Embedded val noteDto: NoteDto,
    @Relation(
        parentColumn = "id",
        entityColumn = "rootID"
    )
    val imageList: List<NoteImage>
)