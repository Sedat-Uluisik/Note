package com.sedat.note.presentation.homefragment.viewmodel

import androidx.lifecycle.ViewModel
import com.sedat.note.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelHomeFragment @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {

    fun noteList() = repository.getMainNotes()

}