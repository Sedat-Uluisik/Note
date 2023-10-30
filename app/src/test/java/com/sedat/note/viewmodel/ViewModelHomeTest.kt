package com.sedat.note.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sedat.note.MainCoroutineRule
import com.sedat.note.presentation.homefragment.viewmodel.ViewModelHomeFragment
import com.sedat.note.repo.FakeNoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

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
}