package com.sedat.note.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.sedat.note.MainCoroutineRule
import com.sedat.note.domain.model.Note
import com.sedat.note.getOrAwaitValue
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import com.sedat.note.repo.FakeNoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ViewModelHomeTest {

    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule//işlemleri main thread da yapmak için kullanılıyor.
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ViewModelHomeFragment

    @Before
    fun setup(){
        viewModel = ViewModelHomeFragment(FakeNoteRepository())
    }

    @Test
    fun `noteListTest`() = runTest{
        val note = Note(3, -1, "txt3", 1000, 0, 0)

        val list = viewModel.noteList().toList()[0]

        assertThat(list).contains(note)
    }

    @Test
    fun `get_sub_notes_test`() = runTest{
        val subNote =  Note(12, 2, "sub2", 1000, 0, 0)

        viewModel.getSubNotes(2)

        assertThat(viewModel.subNoteList.getOrAwaitValue()).contains(subNote)
    }

    @Test
    fun `get_main_notes_test`() = runTest{
        val mainNote =  Note(2, -1, "txt2", 1000, 3, 0)

        viewModel.getMainNotes()

        assertThat(viewModel.subNoteList.getOrAwaitValue()).contains(mainNote)
    }

    @Test
    fun `search_note_test`() = runTest{
        val note1 = Note(1, -1, "txt1", 1000, 0, 2)
        val note2 = Note(2, -1, "txt2", 1000, 3, 0)
        val note3 = Note(3, -1, "txt3", 1000, 0, 0)

        viewModel.searchNote("txt")

        assertThat(viewModel.subNoteList.getOrAwaitValue()).contains(note1)
        assertThat(viewModel.subNoteList.getOrAwaitValue()).contains(note2)
        assertThat(viewModel.subNoteList.getOrAwaitValue()).contains(note3)
    }

    @Test
    fun `search_note_test_v2`() = runTest{
        val note1 = Note(1, -1, "txt1", 1000, 0, 2)
        val note2 = Note(2, -1, "txt2", 1000, 3, 0)
        val note3 = Note(3, -1, "txt3", 1000, 0, 0)

        viewModel.searchNote("1")

        assertThat(viewModel.subNoteList.getOrAwaitValue()).contains(note1)
        assertThat(viewModel.subNoteList.getOrAwaitValue()).doesNotContain(note2)
        assertThat(viewModel.subNoteList.getOrAwaitValue()).doesNotContain(note3)
    }
}