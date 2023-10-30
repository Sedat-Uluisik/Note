package com.sedat.note.roomdb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.sedat.note.domain.database.Dao
import com.sedat.note.domain.database.NoteDatabase
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteDaoTest {
    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: NoteDatabase
    private lateinit var dao: Dao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries().build() //geçici bir veritabanını ram da oluşturur.

        dao = database.dao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun `insert_note_test`() = runTest(){

        val noteDto = NoteDto(1, -1, "test", 100000L)
        dao.saveNote(noteDto)

        val list = mutableListOf<Note>()
        backgroundScope.launch (UnconfinedTestDispatcher(testScheduler) ){
            val noteList = dao.getMainNotes().toList().first()
            list.addAll(noteList)

            val note = Note(1, -1, "test", 100000L, 1, 1)
            assertThat(list).contains(note)
        }
    }

    @Test
    fun `get_note_with_id_test`() = runTest{
        val noteDto = NoteDto(1, -1, "test", 100000L)
        dao.saveNote(noteDto)

        val note = dao.getNoteWithID(1)
        assertThat(note).isEqualTo(noteDto)
    }
}