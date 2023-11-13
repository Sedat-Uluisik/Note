package com.sedat.note.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.sedat.note.MainCoroutineRule
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.getOrAwaitValue
import com.sedat.note.presentation.noteimagesfragment.viewmodel.ViewModelNoteImages
import com.sedat.note.repo.FakeNoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ViewModelNoteImagesTest {
    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ViewModelNoteImages

    @Before
    fun setup(){
        viewModel = ViewModelNoteImages(FakeNoteRepository())
    }

    @Test
    fun getNoteImagesTest() {
        val noteImage = NoteImage(1, 1, "url_1", "desc_1")
        val noteImage2 = NoteImage(2, 1, "url_2", "desc_2")
        viewModel.getNoteImages(1)

        assertThat(viewModel.noteImages.getOrAwaitValue().data?.size).isEqualTo(2)
        assertThat(viewModel.noteImages.getOrAwaitValue().data).contains(noteImage)
        assertThat(viewModel.noteImages.getOrAwaitValue().data).contains(noteImage2)
    }

    @Test
    fun deleteNoteImagePathFromRoom() {
        /*  data
        (NoteImage(1, 1, "url_1", "desc_1"))
        (NoteImage(2, 1, "url_2", "desc_2"))
        (NoteImage(3, 2, "url_3", "desc_3"))
        -------------------------------------------- after delete
        (NoteImage(1, 1, "url_1", "desc_1"))
        (NoteImage(3, 2, "url_3", "desc_3"))
        ------------------------------------------- expected data
        (NoteImage(1, 1, "url_1", "desc_1"))
         */

        val noteImage = NoteImage(1, 1, "url_1", "desc_1")

        viewModel.deleteNoteImagePathFromRoom(1, 2)

        assertThat(viewModel.noteImages.getOrAwaitValue().data?.size).isEqualTo(1)
        assertThat(viewModel.noteImages.getOrAwaitValue().data).contains(noteImage)
    }
}