package com.sedat.note.domain.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sedat.note.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun saveNote(note: Note): Long

    @Query("SELECT * FROM T_Notes WHERE rootID = 1")
    fun getMainNotes(): Flow<List<Note>>
}