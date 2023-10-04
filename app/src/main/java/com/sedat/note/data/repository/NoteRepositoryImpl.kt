package com.sedat.note.data.repository

import android.content.Context
import com.sedat.note.R
import com.sedat.note.domain.database.Dao
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.NoteWithSubNoteInfo
import com.sedat.note.domain.model.Relationships
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

    override suspend fun saveImageFilePathToRoomDB(noteImage: NoteImage): Resource<Boolean> {
        return try {
            val result = dao.saveImageFilePathToRoomDB(noteImage)
            if(result != null)
                Resource.Success(true)
            else
                Resource.Error(context.getString(R.string.image_path_not_save_to_room_db))
        }catch (ex: Exception){
            Resource.Error(context.getString(R.string.image_path_not_save_to_room_db))
        }
    }

    override fun getMainNotes() = dao.getMainNotes()
    override suspend fun getSubNotes(rootID: Int): List<NoteWithSubNoteInfo>{
        return try {
            dao.getSubNotes(rootID)
        }catch (e: Exception){
            listOf()
        }
    }

    override suspend fun getMainNotesV2(): Resource<List<NoteWithSubNoteInfo>> {
        return try {
            val result = dao.getMainNotesV2()
            Resource.Success(result)
        }catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }

    override suspend fun getSubNotesForDeleting(rootId: Int): List<Note> {
        return dao.getSubNotesForDeleting(rootId)
    }

    override suspend fun getNoteImages(rootId: Int): Resource<List<NoteImage>> {
        return try {
            val imageList = dao.getNoteImages(rootId)
            Resource.Success(imageList)
        }catch (ex: Exception){
            Resource.Error(context.getString(R.string.there_was_a_problem_retrieving_the_images))
        }
    }

    override suspend fun deleteNote(id: Int) {
        dao.deleteNote(id)
    }

    override suspend fun deleteRelationship(subId: Int) {
        dao.deleteRelationship(subId)
    }

    override suspend fun getNoteWithID(noteID: Int): Resource<Note> {
        return try {
            val result = dao.getNoteWithID(noteID)
            Resource.Success(result)
        }catch (e: Exception){
            Resource.Error(context.getString(R.string.note_could_be_not_found))
        }
    }

    override suspend fun updateNote(id: Int, _text: String, _time: Long): Resource<Boolean> {
        return try {
            val result = dao.updateNote(id, _text, _time)
            println(result)
            if(result != null)
                Resource.Success(true)
            else
                Resource.Error(context.getString(R.string.note_update_is_not_successful))
        }catch (e: Exception){
            Resource.Error(context.getString(R.string.note_update_is_not_successful))
        }
    }

    override suspend fun saveSubNote(note: Note): Resource<Long?> {
        return try {
            val result = dao.saveNote(note)
            Resource.Success(result)
        }catch (e: Exception){
            Resource.Error(context.getString(R.string.note_saved_fail))
        }
    }

    override suspend fun saveRelationship(relationships: Relationships): Resource<Long?> {
        return try {
            val result = dao.createRelationship(relationships)
            Resource.Success(result)
        }catch (e: Exception){
            Resource.Error(context.getString(R.string.note_saved_fail))
        }
    }


}