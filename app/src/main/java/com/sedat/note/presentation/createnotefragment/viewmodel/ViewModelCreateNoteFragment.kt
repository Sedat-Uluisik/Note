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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelCreateNoteFragment @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _isSaveSuccessful = MutableLiveData<Resource<Boolean>>()
    val isSaveSuccessful: LiveData<Resource<Boolean>> get() = _isSaveSuccessful

    fun saveNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveNote(note).collect {
            withContext(Dispatchers.Main){
                _isSaveSuccessful.postValue(it)
            }
        }
    }

    private val _selectedNote = MutableLiveData<Resource<Note>>()
    val selectedNote: LiveData<Resource<Note>> get() = _selectedNote
    fun getNoteWithID(selectedNoteID: Int) = viewModelScope.launch(Dispatchers.IO) {
        _selectedNote.postValue(Resource.Loading())
        val data = repository.getNoteWithID(selectedNoteID) //Veriler IO thread ile alınır.
        withContext(Dispatchers.Main){ //Alınan veriler UI thread da liveData ya gönderilir ve ui güncellemesi yapılır.
            _selectedNote.postValue(data) //postValue ile liveData'nın değeri UI Thread üzerinde güvenli bir şekilde güncellenbilir.
        }
    }

    /*
    * setValue -> liveData'nın değerini anında günceller ve sadece UI Thread üzerinde çalışır.
    * postValue -> liveData'nın değerini UI Thread dışında da güncellemek için kullanılır fakat burada anında güncelleme işlemi yapılmaz
      UI Thread uygun olduğunda güncelleme işlemi yapılır.
    * liveData'nın değerini UI Thread ile güncellemek daha güvenlidir.
     */

    fun updateNote(id: Int, text: String, time: Long) = viewModelScope.launch(Dispatchers.IO) {
        _isSaveSuccessful.postValue(Resource.Loading())
        val result = repository.updateNote(id, text, time)
        withContext(Dispatchers.Main){
            _isSaveSuccessful.postValue(result)
        }
    }

    fun saveSubNote(note: Note, rootID: Int) = viewModelScope.launch(Dispatchers.IO) {
        val saveNote: Resource<Long?>
        val createRelationship: Resource<Long?>

        val call1 = async { repository.saveSubNote(note) }
        saveNote = call1.await()
        saveNote.data?.let {
            val relationships = Relationships(0, rootID, it.toInt())
            val call2 = async { repository.saveRelationship(relationships) }
            createRelationship = call2.await()

            withContext(Dispatchers.Main){
                if(createRelationship.data != null)
                    _isSaveSuccessful.value = Resource.Success(true)
                else
                    _isSaveSuccessful.value = Resource.Success(false)
            }
        }
    }
}