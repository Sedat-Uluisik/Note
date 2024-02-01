package com.sedat.note.domain.repository

import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.NoteWithImages
import com.sedat.note.domain.model.Relationships
import com.sedat.note.util.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun saveNote(note: NoteDto): Flow<Resource<Long?>>
    suspend fun saveImageFilePathToRoomDB(noteImage: NoteImage): Resource<Boolean>
    fun getMainNotes(): Flow<List<Note>>

    suspend fun getSubNotes(rootID: Int): List<Note>

    suspend fun getMainNotesV2(): Resource<List<Note>>

    suspend fun getSubNotesForDeleting(rootId: Int): List<NoteWithImages>

    suspend fun getMainNoteAndImagesForDeleting(noteId: Int): NoteWithImages
    suspend fun getNoteImages(rootId: Int): Resource<List<NoteImage>>
    suspend fun deleteNote(id: Int)
    suspend fun deleteRelationship(subId: Int)

    suspend fun getNoteWithID(noteID: Int): Resource<Note>

    suspend fun updateNote(id: Int, _text: String, _time: Long, color: String): Resource<Int>
    suspend fun deleteNoteImagePathFromRoom(imageId: Int): Resource<Boolean>

    suspend fun saveSubNote(note: NoteDto): Resource<Long?>
    suspend fun saveRelationship(relationships: Relationships): Resource<Long?>
    suspend fun searchNote(searchQuery: String): Resource<List<Note>>

}