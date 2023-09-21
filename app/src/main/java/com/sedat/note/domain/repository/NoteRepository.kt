package com.sedat.note.domain.repository

import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteWithSubNoteInfo
import com.sedat.note.domain.model.Relationships
import com.sedat.note.util.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun saveNote(note: Note): Flow<Resource<Boolean>>
    fun getMainNotes(): Flow<List<NoteWithSubNoteInfo>>

    suspend fun getSubNotes(rootID: Int): List<NoteWithSubNoteInfo>

    fun getNotes(noteID: Int): NoteWithSubNoteInfo

    suspend fun getSubNotesForDeleting(rootId: Int): List<Note>
    suspend fun deleteNote(id: Int)
    suspend fun deleteRelationship(subId: Int)

    suspend fun getNoteWithID(noteID: Int): Resource<Note>

    suspend fun updateNote(id: Int, _text: String, _time: Long): Resource<Boolean>

    suspend fun saveSubNote(note: Note): Resource<Long?>
    suspend fun saveRelationship(relationships: Relationships): Resource<Long?>

    suspend fun deleteNoteORSubNotes(noteId: Int, relationshipId: Int)

}