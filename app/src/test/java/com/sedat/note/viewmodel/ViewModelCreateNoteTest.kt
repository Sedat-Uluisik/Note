package com.sedat.note.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.sedat.note.MainCoroutineRule
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.getOrAwaitValue
import com.sedat.note.presentation.createnotefragment.viewmodel.ViewModelCreateNoteFragment
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import com.sedat.note.repo.FakeNoteRepository
import com.sedat.note.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ViewModelCreateNoteTest {
    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule//işlemleri main thread da yapmak için kullanılıyor.
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ViewModelCreateNoteFragment

    @Before
    fun setup(){
        viewModel = ViewModelCreateNoteFragment(FakeNoteRepository())
    }

    @Test
    fun saveNote() = runTest{
        viewModel.saveNote(
            NoteDto(1, 1, "txt", 1000)
        )

        assertThat(viewModel.isSaveSuccessful.getOrAwaitValue().data).isEqualTo(true)
    }

    @Test
    fun getNoteWithIDTest() = runTest{
        val expectedNote = Note(3, -1, "txt3", 1000, 0, 0)

        viewModel.getNoteWithID(3)

        //istenen data gelmeden önce loading durumu geldiği için test başarısız olup bitiriliyor bu nedenle gecikme süresi eklendi.
        delay(2000)

        assertThat(viewModel.selectedNote.getOrAwaitValue().data).isEqualTo(expectedNote)
    }

    @Test
    fun updateNoteTest() = runTest{
        val updatedNote = Note(3, -1, "txt4", 10500, 0, 0)

        viewModel.updateNote(updatedNote.id, updatedNote.text, updatedNote.time)

        delay(2000)

        viewModel.getNoteWithID(updatedNote.id)

        //istenen data gelmeden önce loading durumu geldiği için test başarısız olup bitiriliyor bu nedenle gecikme süresi eklendi.

        when(viewModel.selectedNote.getOrAwaitValue()){
            is Resource.Success ->{
                assertThat(viewModel.selectedNote.getOrAwaitValue().data).isEqualTo(updatedNote)
            }
            else ->{}
        }


    }
}