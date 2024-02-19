package com.sedat.note.presentation.homefragment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteWithImages
import com.sedat.note.domain.repository.NoteRepository
import com.sedat.note.util.Event
import com.sedat.note.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ViewModelHomeFragment @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    fun noteList() = repository.getMainNotes()

    private var _subNoteList = MutableLiveData<Event<List<Note>>>()
    val subNoteList: LiveData<Event<List<Note>>> get() = _subNoteList

    private var _mainNoteList = MutableLiveData<Event<List<Note>>>()
    val mainNoteList: LiveData<Event<List<Note>>> get() = _mainNoteList

    private var _rootIDList = MutableLiveData<Event<ArrayList<Int>>>()
    val rootIDList: LiveData<Event<ArrayList<Int>>> get() = _rootIDList

    fun insertRootIdFromRootIDList(id: Int) = viewModelScope.launch{
        val hold = rootIDList.value?.getContentIfNotHandled() ?: arrayListOf()
        hold.add(id)
        _rootIDList.postValue(Event(hold))
    }

    fun deleteLastRootIdFromRootIDList() = viewModelScope.launch{
        val hold = rootIDList.value?.getContentIfNotHandled()
        hold?.let {
            if(it.isNotEmpty()){
                it.removeLast()
                _rootIDList.postValue(Event(it))
            }else
                _rootIDList.postValue(Event(arrayListOf()))
        } ?: _rootIDList.postValue(Event(arrayListOf()))
    }

    fun getSubNotes(rootID: Int) = viewModelScope.launch(Dispatchers.IO) {
        println("subnotes")
        val data = repository.getSubNotes(rootID)
        withContext(Dispatchers.Main){
            println(data)
            _subNoteList.postValue(Event(data))
        }
    }

    fun getMainNotes() = viewModelScope.launch(Dispatchers.IO) {
        println("mainnotes")
        val result = repository.getMainNotesV2()
        withContext(Dispatchers.Main){
            when(result){
                is Resource.Success ->{
                    _mainNoteList.postValue(Event(result.data ?: listOf()))
                }
                else ->{
                    _mainNoteList.postValue(Event(listOf()))
                }
            }
        }
    }

    fun deleteNoteAndSubNotes(noteIdToDelete: Int, rootId: Int) = viewModelScope.launch(Dispatchers.IO){
        val subNotes = mutableListOf<NoteWithImages>()
        val stack = mutableListOf(noteIdToDelete)

        while(stack.isNotEmpty()){
            val parentId = stack.removeAt(0)
            val subNoteIds = repository.getSubNotesForDeleting(parentId)
            subNotes.addAll(subNoteIds)
            stack.addAll(subNoteIds.map { it.noteDto.id })
        }

        if(subNotes.isEmpty())
            repository.deleteRelationship(noteIdToDelete)

        //delete all sub notes
        subNotes.forEach {note ->
            repository.deleteNote(note.noteDto.id)
            repository.deleteRelationship(note.noteDto.id)
            note.imageList.forEach {
                repository.deleteNoteImagePathFromRoom(it.id)
                deleteImageFile(it.imageFileUrl)
            }
        }

        //delete main note
        val mainNoteList = repository.getMainNoteAndImagesForDeleting(noteIdToDelete)
        mainNoteList.imageList.forEach {
            repository.deleteNoteImagePathFromRoom(it.id)
            deleteImageFile(it.imageFileUrl)
        }
        repository.deleteNote(noteIdToDelete)

        getSubNotes(rootId)
    }

    private fun deleteImageFile(imagePath: String){
        try {
            val file = File(imagePath)
            if(file.exists())
                file.delete()
        }catch (e: Exception){
            val file = File(imagePath)
            if(file.exists())
                file.delete()
        }
    }

    fun searchNote(searchQuery: String) = viewModelScope.launch(Dispatchers.IO) {
        println("search")
        val result = repository.searchNote(searchQuery)
        withContext(Dispatchers.Main){
            when(result){
                is Resource.Success ->{
                    _subNoteList.postValue(Event(result.data ?: listOf()))
                }
                else ->{
                    _subNoteList.postValue(Event(listOf()))
                }
            }
        }
    }
}