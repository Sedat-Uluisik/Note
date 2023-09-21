package com.sedat.note.domain.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteWithSubNoteInfo
import com.sedat.note.domain.model.Relationships
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun saveNote(note: Note): Long?

    @Transaction
    @Query("SELECT * FROM T_Notes WHERE rootID = -1")
    fun getMainNotes(): Flow<List<NoteWithSubNoteInfo>>

    @Transaction
    @Query("SELECT * FROM T_Notes WHERE rootID = -1")
    suspend fun getMainNotesV2(): List<NoteWithSubNoteInfo>

    @Query("SELECT * FROM T_Notes WHERE id = :noteID")
    suspend fun getNoteWithID(noteID: Int): Note

    @Transaction
    @Query("SELECT * FROM T_Notes WHERE rootID = :rootID")
    suspend fun getSubNotes(rootID: Int): List<NoteWithSubNoteInfo>

    @Query("UPDATE T_Notes SET text = :text, time = :time WHERE id = :id")
    suspend fun updateNote(id: Int, text: String, time: Long): Int?

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun createRelationship(relationships: Relationships): Long?

    @Query("SELECT * FROM T_Notes WHERE rootID = :rootId")
    suspend fun getSubNotesForDeleting(rootId: Int): List<Note>

    @Query("DELETE FROM T_Notes WHERE id = :id")
    suspend fun deleteNote(id: Int)

    @Query("DELETE FROM T_Relationships WHERE subID = :subId")
    suspend fun deleteRelationship(subId: Int)

}