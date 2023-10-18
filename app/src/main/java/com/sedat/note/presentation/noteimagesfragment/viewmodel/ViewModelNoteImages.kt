package com.sedat.note.presentation.noteimagesfragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelNoteImages @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    private val _noteImages = MutableLiveData<Resource<List<NoteImage>>>()
    val noteImages: LiveData<Resource<List<NoteImage>>> get() = _noteImages

    fun getNoteImages(noteId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val imageList = repository.getNoteImages(rootId = noteId)
        withContext(Dispatchers.Main){
            _noteImages.postValue(imageList)
        }
    }

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    fun deleteNoteImagePathFromRoom(noteId: Int, imageId: Int) = viewModelScope.launch(Dispatchers.IO) {
        when(val result = repository.deleteNoteImagePathFromRoom(imageId)){
            is Resource.Success ->{
                getNoteImages(noteId)
            }
            is Resource.Loading ->{}
            is Resource.Error ->{
                withContext(Dispatchers.Main){
                    _message.postValue(result.message ?: "error")
                }
            }
        }

    }

}