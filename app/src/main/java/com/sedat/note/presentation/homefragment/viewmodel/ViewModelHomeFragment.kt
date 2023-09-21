package com.sedat.note.presentation.homefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteWithSubNoteInfo
import com.sedat.note.domain.model.Relationships
import com.sedat.note.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelHomeFragment @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    fun noteList() = repository.getMainNotes()

    private var _subNoteList = MutableLiveData<List<NoteWithSubNoteInfo>>()
    val subNoteList: LiveData<List<NoteWithSubNoteInfo>> get() = _subNoteList
    fun getSubNotes(rootID: Int) = viewModelScope.launch {
        _subNoteList.value = repository.getSubNotes(rootID)
    }

    fun deleteNoteAndSubNotes(noteIdToDelete: Int) = viewModelScope.launch{
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