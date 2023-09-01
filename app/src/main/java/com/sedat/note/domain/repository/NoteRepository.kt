package com.sedat.note.domain.repository

import com.sedat.note.domain.model.Note
import com.sedat.note.util.Resource
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun saveNote(note: Note): Flow<Resource<Boolean>>
    fun getMainNotes(): Flow<List<Note>>
}