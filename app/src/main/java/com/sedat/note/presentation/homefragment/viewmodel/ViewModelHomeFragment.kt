package com.sedat.note.presentation.homefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteWithSubNoteInfo
import com.sedat.note.domain.model.Relationships
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelHomeFragment @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    fun noteList() = repository.getMainNotes()

    private var _subNoteList = MutableLiveData<List<NoteWithSubNoteInfo>>()
    val subNoteList: LiveData<List<NoteWithSubNoteInfo>> get() = _subNoteList
    fun getSubNotes(rootID: Int) = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.getSubNotes(rootID)
        withContext(Dispatchers.Main){
            _subNoteList.postValue(data)
        }
    }

    fun getMainNotes() = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.getMainNotesV2()
        withContext(Dispatchers.Main){
            when(result){
                is Resource.Success ->{
                    _subNoteList.postValue(result.data ?: listOf())
                }
                else ->{
                    _subNoteList.postValue(listOf())
                }
            }
        }
    }

    fun deleteNoteAndSubNotes(noteIdToDelete: Int) = viewModelScope.launch(Dispatchers.IO){
        val subNotes = mutableListOf<Note>()
        val stack = mutableListOf(noteIdToDelete)

        while(stack.isNotEmpty()){
            val parentId = stack.removeAt(0)
            val subNoteIds = repository.getSubNotesForDeleting(parentId)
            subNotes.addAll(subNoteIds)
            stack.addAll(subNoteIds.map { it.id })
        }

        //delete all sub notes
        subNotes.forEach {note ->
            repository.deleteNote(note.id)
            repository.deleteRelationship(note.id)
        }

        //delete main note
        repository.deleteNote(noteIdToDelete)
    }
}