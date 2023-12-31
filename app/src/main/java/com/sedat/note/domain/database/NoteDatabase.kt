package com.sedat.note.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.Relationships

@Database(entities = [NoteDto::class, NoteImage::class, Relationships::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun dao(): Dao
}