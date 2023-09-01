package com.sedat.note.presentation.createnotefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelCreateNoteFragment @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    private val _isSaveSuccessful = MutableLiveData<Resource<Boolean>>()
    val isSaveSuccessful: LiveData<Resource<Boolean>> get() = _isSaveSuccessful

    fun saveNote(note: Note) = viewModelScope.launch {
       repository.saveNote(note).collect{
           _isSaveSuccessful.value = it
       }
    }
}