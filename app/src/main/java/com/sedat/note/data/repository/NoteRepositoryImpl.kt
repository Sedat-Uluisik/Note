package com.sedat.note.data.repository

import android.content.Context
import com.sedat.note.R
import com.sedat.note.domain.database.Dao
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    val dao: Dao,
    val context: Context
    ): NoteRepository {
    override suspend fun saveNote(note: Note): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            try {
                val result = dao.saveNote(note)
                if(result != null)
                    emit(Resource.Success((true)))
                else
                    emit(Resource.Error(context.getString(R.string.note_saved_fail)))
            }catch (e: Exception){
                emit(Resource.Error(context.getString(R.string.note_saved_fail)))
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getMainNotes() = dao.getMainNotes()


}