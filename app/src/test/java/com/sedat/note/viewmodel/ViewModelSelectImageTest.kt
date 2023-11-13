package com.sedat.note.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.sedat.note.MainCoroutineRule
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.getOrAwaitValue
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import com.sedat.note.presentation.selectimagefragment.viewmodel.ViewModelSelectImage
import com.sedat.note.repo.FakeNoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ViewModelSelectImageTest {
    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule//işlemleri main thread da yapmak için kullanılıyor.
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ViewModelSelectImage

    @Before
    fun setup(){
        viewModel = ViewModelSelectImage(FakeNoteRepository())
    }

    @Test
    fun saveImagePathToRoomDB(){
        viewModel.saveImagePathToRoomDB(1, "url", "desc")
        assertThat(viewModel.isImageSaveToRoomDBSuccessful.getOrAwaitValue().data).isEqualTo(true)
    }
}