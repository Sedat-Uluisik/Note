package com.sedat.note.domain.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.NoteWithImages
import com.sedat.note.domain.model.Relationships
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun saveNote(note: NoteDto): Long?

    @Query("SELECT  Note.id, Note.rootID, Note.text, Note.time, Note.color," +
            "(SELECT COUNT(id) from T_Relationships WHERE rootID = Note.id ) AS subNoteCount, " +
            "(SELECT COUNT(id) from T_NoteImage WHERE rootID = Note.id ) AS imageCount  " +
            "FROM T_Notes AS Note WHERE Note.rootID = -1")
    fun getMainNotes(): Flow<List<Note>>

    @Query("SELECT  Note.id, Note.rootID, Note.text, Note.time, Note.color," +
            "(SELECT COUNT(id) from T_Relationships WHERE rootID = Note.id ) AS subNoteCount, " +
            "(SELECT COUNT(id) from T_NoteImage WHERE rootID = Note.id ) AS imageCount  " +
            "FROM T_Notes AS Note WHERE Note.rootID = -1")
    suspend fun getMainNotesV2(): List<Note>

    @Query("SELECT * FROM T_Notes WHERE id = :noteID")
    suspend fun getNoteWithID(noteID: Int): NoteDto

    @Query("SELECT  Note.id, Note.rootID, Note.text, Note.time, Note.color," +
            "(SELECT COUNT(id) from T_Relationships WHERE rootID = Note.id ) AS subNoteCount, " +
            "(SELECT COUNT(id) from T_NoteImage WHERE rootID = Note.id ) AS imageCount  " +
            "FROM T_Notes AS Note WHERE Note.rootID = :rootID")
    suspend fun getSubNotes(rootID: Int): List<Note>

    @Query("SELECT * FROM T_NoteImage WHERE rootID = :rootId")
    suspend fun getNoteImages(rootId: Int): List<NoteImage>

    @Query("UPDATE T_Notes SET text = :text, time = :time, color = :color WHERE id = :id")
    suspend fun updateNote(id: Int, text: String, time: Long, color: String): Int?

    @Query("UPDATE T_NoteImage SET description = :desc WHERE id = :id")
    suspend fun updateNoteImageDesc(id: Int, desc: String): Int?

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun createRelationship(relationships: Relationships): Long?

    @Query("SELECT * FROM T_Notes WHERE rootID = :rootId")
    suspend fun getSubNotesForDeleting(rootId: Int): List<NoteWithImages>

    @Query("SELECT * FROM T_Notes WHERE id = :noteId")
    suspend fun getMainNoteAndImagesForDeleting(noteId: Int): NoteWithImages

    @Query("DELETE FROM T_Notes WHERE id = :id")
    suspend fun deleteNote(id: Int)

    @Query("DELETE FROM T_Relationships WHERE subID = :subId")
    suspend fun deleteRelationship(subId: Int)

    @Query("DELETE FROM T_NoteImage WHERE id = :imageId")
    suspend fun deleteNoteImagePathFromRoom(imageId: Int): Int

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun saveImageFilePathToRoomDB(noteImage: NoteImage): Long?

    @Query("SELECT  Note.id, Note.rootID, Note.text, Note.time, Note.color," +
            "(SELECT COUNT(id) from T_Relationships WHERE rootID = Note.id ) AS subNoteCount, " +
            "(SELECT COUNT(id) from T_NoteImage WHERE rootID = Note.id ) AS imageCount  " +
            "FROM T_Notes AS Note " +
            "LEFT JOIN T_NoteImage ON T_NoteImage.rootID = Note.id " +
            "WHERE Note.text LIKE '%' || :searchQuery || '%' OR T_NoteImage.description LIKE '%' || :searchQuery || '%'")
    suspend fun searchNote(searchQuery: String): List<Note>

}