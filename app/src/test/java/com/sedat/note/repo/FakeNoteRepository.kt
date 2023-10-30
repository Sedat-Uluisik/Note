package com.sedat.note.repo

import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.NoteWithImages
import com.sedat.note.domain.model.Relationships
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import kotlinx.coroutines.flow.Flow

class FakeNoteRepository: NoteRepository {
    override suspend fun saveNote(note: NoteDto): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveImageFilePathToRoomDB(noteImage: NoteImage): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMainNotes(): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSubNotes(rootID: Int): List<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun getMainNotesV2(): Resource<List<Note>> {
        TODO("Not yet implemented")
    }

    override suspend fun getSubNotesForDeleting(rootId: Int): List<NoteWithImages> {
        TODO("Not yet implemented")
    }

    override suspend fun getMainNoteAndImagesForDeleting(noteId: Int): NoteWithImages {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteImages(rootId: Int): Resource<List<NoteImage>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRelationship(subId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteWithID(noteID: Int): Resource<Note> {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(id: Int, _text: String, _time: Long): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNoteImagePathFromRoom(imageId: Int): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveSubNote(note: NoteDto): Resource<Long?> {
        TODO("Not yet implemented")
    }

    override suspend fun saveRelationship(relationships: Relationships): Resource<Long?> {
        TODO("Not yet implemented")
    }

    override suspend fun searchNote(searchQuery: String): Resource<List<Note>> {
        TODO("Not yet implemented")
    }
}