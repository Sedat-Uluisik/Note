package com.sedat.note.presentation.createnotefragment.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.R
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.Relationships
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelCreateNoteFragment @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _isSaveSuccessful = MutableLiveData<Resource<Boolean>>()
    val isSaveSuccessful: LiveData<Resource<Boolean>> get() = _isSaveSuccessful

    fun saveNote(note: Note) = viewModelScope.launch {
        repository.saveNote(note).collect {
            _isSaveSuccessful.value = it
        }
    }

    private val _selectedNote = MutableLiveData<Resource<Note>>()
    val selectedNote: LiveData<Resource<Note>> get() = _selectedNote
    fun getNoteWithID(selectedNoteID: Int) = viewModelScope.launch {
        _selectedNote.value = Resource.Loading()
        _selectedNote.value = repository.getNoteWithID(selectedNoteID)
    }

    fun updateNote(id: Int, text: String, time: Long) = viewModelScope.launch {
        _isSaveSuccessful.value = Resource.Loading()
        _isSaveSuccessful.value = repository.updateNote(id, text, time)
    }

    fun saveSubNote(note: Note, rootID: Int) = viewModelScope.launch {
        val saveNote: Resource<Long?>
        val createRelationship: Resource<Long?>

        val call1 = async { repository.saveSubNote(note) }
        saveNote = call1.await()
        saveNote.data?.let {
            val relationships = Relationships(0, rootID, it.toInt())
            val call2 = async { repository.saveRelationship(relationships) }
            createRelationship = call2.await()

            if(createRelationship.data != null)
                _isSaveSuccessful.value = Resource.Success(true)
            else
                _isSaveSuccessful.value = Resource.Success(false)
        }
    }
}