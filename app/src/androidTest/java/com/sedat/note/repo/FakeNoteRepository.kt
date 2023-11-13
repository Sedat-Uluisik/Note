package com.sedat.note.repo

import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.NoteWithImages
import com.sedat.note.domain.model.Relationships
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNoteRepository: NoteRepository {

    private val noteImageList = mutableListOf<NoteImage>(
        NoteImage(1, 1, "url_1", "desc_1"),
        NoteImage(2, 1, "url_2", "desc_2"),
        NoteImage(3, 2, "url_3", "desc_3")
    )

    private val mainNoteList = mutableListOf(
        Note(1, -1, "txt1", 1000, 0, 2),
        Note(2, -1, "txt2", 1000, 3, 0),
        Note(3, -1, "txt3", 1000, 0, 0)
    )

    private val subNoteList = mutableListOf(
        Note(11, 2, "sub1", 1000, 0, 0),
        Note(12, 2, "sub2", 1000, 0, 0),
        Note(13, 2, "sub3", 1000, 0, 0)
    )

    override suspend fun saveNote(note: NoteDto): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Success(true))
        }
    }

    override suspend fun saveImageFilePathToRoomDB(noteImage: NoteImage): Resource<Boolean> {
        return Resource.Success(true)
    }

    override fun getMainNotes(): Flow<List<Note>> {
        return flow {
            emit(mainNoteList.toList())
        }
    }

    override suspend fun getSubNotes(rootID: Int): List<Note> {
        return subNoteList.filter { it.rootID == rootID }
    }

    override suspend fun getMainNotesV2(): Resource<List<Note>> {
        return Resource.Success(mainNoteList)
    }

    override suspend fun getSubNotesForDeleting(rootId: Int): List<NoteWithImages> {
        return listOf(
            NoteWithImages(
                NoteDto(11, 2, "sub1", 1000),
                listOf(
                    NoteImage(1, 1, "url_1", "desc_1"),
                    NoteImage(2, 1, "url_2", "desc_2"),
                )
            )
        )
    }

    override suspend fun getMainNoteAndImagesForDeleting(noteId: Int): NoteWithImages {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteImages(rootId: Int): Resource<List<NoteImage>> {
        return Resource.Success(noteImageList.filter { it.rootID == rootId })
    }

    override suspend fun deleteNote(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRelationship(subId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteWithID(noteID: Int): Resource<Note> {
        return Resource.Success(
            mainNoteList.first { it.id == noteID }
        )
    }

    override suspend fun updateNote(id: Int, _text: String, _time: Long): Resource<Boolean> {
        val note = mainNoteList.first{ it.id == id }
        note.text = _text
        note.time = _time

        return Resource.Success(true)
    }

    override suspend fun deleteNoteImagePathFromRoom(imageId: Int): Resource<Boolean> {
        noteImageList.removeIf {
            it.id == imageId
        }

        return Resource.Success(true)
    }

    override suspend fun saveSubNote(note: NoteDto): Resource<Long?> {
        TODO("Not yet implemented")
    }

    override suspend fun saveRelationship(relationships: Relationships): Resource<Long?> {
        TODO("Not yet implemented")
    }

    override suspend fun searchNote(searchQuery: String): Resource<List<Note>> {
        return Resource.Success(
            mainNoteList.filter {
                it.text.contains(searchQuery)
            }
        )
    }
}