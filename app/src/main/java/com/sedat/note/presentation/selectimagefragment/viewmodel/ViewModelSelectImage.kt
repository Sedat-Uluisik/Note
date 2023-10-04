package com.sedat.note.presentation.selectimagefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelSelectImage @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    private val _isImageSaveToRoomDBSuccessful = MutableLiveData<Resource<Boolean>>()
    val isImageSaveToRoomDBSuccessful: LiveData<Resource<Boolean>> get() = _isImageSaveToRoomDBSuccessful

    fun saveImagePathToRoomDB(noteId: Int, imagePath: String, description: String) = viewModelScope.launch(Dispatchers.IO) {
        val noteImage = NoteImage(id = 0, rootID = noteId, imageFileUrl = imagePath ,description)
        val result = repository.saveImageFilePathToRoomDB(noteImage)
        withContext(Dispatchers.Main){
            _isImageSaveToRoomDBSuccessful.postValue(result)
        }
    }

}