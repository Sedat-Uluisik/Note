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

    private fun getAllSubcategoriesRecursive(noteId: Int): List<Relationships>{
        val subcategories = repository.getNotes(noteId)
        val allSubcategories = arrayListOf<Relationships>()

        for (subcategory in subcategories.subNoteList) {
            allSubcategories.add(subcategory)
            allSubcategories.addAll(getAllSubcategoriesRecursive(subcategory.subID))
        }

        return allSubcategories
    }

    private suspend fun deleteSubcategoriesRecursive(categoryId: Int) {
        val subcategories = getAllSubcategoriesRecursive(categoryId)

        for (subcategory in subcategories) {
            repository.deleteNoteORSubNotes(subcategory.subID, subcategory.id)
        }
    }

    fun deleteCategoryWithSubcategoriesRecursive(categoryId: Int) = viewModelScope.launch {
        // Alt kategorileri sil
        deleteSubcategoriesRecursive(categoryId)

        // Ana kategoriyi sil
        repository.deleteNoteORSubNotes(categoryId, categoryId)
    }

}