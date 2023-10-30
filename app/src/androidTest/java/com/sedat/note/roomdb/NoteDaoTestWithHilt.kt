package com.sedat.note.roomdb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.sedat.note.domain.database.Dao
import com.sedat.note.domain.database.NoteDatabase
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteDto
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.domain.model.Relationships
import dagger.hilt.android.testing.HiltAndroidRule
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
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
class NoteDaoTestWithHilt {

    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("TestDatabase")
    lateinit var database: NoteDatabase
    private lateinit var dao: Dao

    @Before
    fun setup(){
        hiltRule.inject()
        dao = database.dao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun `insert_note_and_get_notes_with_flow`() = runTest(){

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
    fun `insert_note_and_get_notes_with_list`() = runTest(){

        val noteDto = NoteDto(1, -1, "test", 100000L)
        val noteDto2 = NoteDto(2, -1, "test2", 200000L)
        dao.saveNote(noteDto)
        dao.saveNote(noteDto2)

        val list = dao.getMainNotesV2()
        val note = Note(1, -1, "test", 100000L, 0, 0)
        assertThat(list).contains(note)
    }

    @Test
    fun `get_note_with_id_test`() = runTest{
        val noteDto = NoteDto(1, -1, "test", 100000L)
        dao.saveNote(noteDto)

        val note = dao.getNoteWithID(1)
        assertThat(note).isEqualTo(noteDto)
    }

    @Test
    fun `get_sub_notes_test`() = runTest(){

        val rootNote = NoteDto(1, -1, "test", 100000L)
        val rootNote2 = NoteDto(2, -1, "test2", 200000L)
        val subNote = NoteDto(3, 1, "subNote", 2030000L)
        val subNote2 = NoteDto(4, 1, "subNote2", 2004000L)
        val relation = Relationships(1, 1, 3)
        val relation2 = Relationships(2, 1, 4)
        dao.saveNote(rootNote)
        dao.saveNote(rootNote2)
        dao.saveNote(subNote)
        dao.saveNote(subNote2)
        dao.createRelationship(relation)
        dao.createRelationship(relation2)

        val list = dao.getSubNotes(1)
        val expectedSubNote = Note(3, 1, "subNote", 2030000L, 0, 0)
        val expectedSubNote2 = Note(4, 1, "subNote2", 2004000L, 0, 0)
        assertThat(list).contains(expectedSubNote)
        assertThat(list).contains(expectedSubNote2)
    }

    @Test
    fun `save_note_images_and_get_note_images_test`() = runTest{
        val noteImage = NoteImage(1, 1, "url", "desc")
        dao.saveImageFilePathToRoomDB(noteImage)

        val noteImages = dao.getNoteImages(1)
        assertThat(noteImages).contains(noteImage)
    }

    @Test
    fun `update_note_test`() = runTest{
        val noteDto = NoteDto(1, -1, "test", 100000L)
        val noteDtoNew = NoteDto(1, -1, "test2", 200000L)
        dao.saveNote(noteDto)
        dao.saveNote(noteDto2)

        val list = dao.getMainNotesV2()
        val note = Note(1, -1, "test", 100000L, 0, 0)
        assertThat(list).contains(note)
    }
}