package com.sedat.note.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.sedat.note.R
import com.sedat.note.domain.model.ActionType
import com.sedat.note.launchFragmentInHiltContainer
import com.sedat.note.presentation.createnotefragment.CreateNoteFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@HiltAndroidTest
@ExperimentalCoroutinesApi
class CreateNoteFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        hiltRule.inject()
    }

    @Test
    fun testBackButtonIsDisplayed(){
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("type" to ActionType.CREATE_NEW_NOTE, "selected_note_id" to 1)

        launchFragmentInHiltContainer<CreateNoteFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationCreateNoteFragmentToHomeFragmentWhenIClickedBackButton(){
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("type" to ActionType.CREATE_NEW_NOTE, "selected_note_id" to 1)

        launchFragmentInHiltContainer<CreateNoteFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(withId(R.id.backBtn)).perform(click())

        //Espresso.pressBack()

        Mockito.verify(navController).popBackStack()
    }

    @Test
    fun titleTextIsDisplayed(){
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("type" to ActionType.CREATE_NEW_NOTE, "selected_note_id" to 1)

        launchFragmentInHiltContainer<CreateNoteFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(withId(R.id.txt_title)).check(matches(isDisplayed()))
        onView(withId(R.id.txt_title)).check(matches(withText("Title")))
    }

    @Test
    fun noteEdittextIsDisplayed(){
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("type" to ActionType.CREATE_NEW_NOTE, "selected_note_id" to 1)

        launchFragmentInHiltContainer<CreateNoteFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(withId(R.id.edt_note)).check(matches(isDisplayed()))
    }

    @Test
    fun `save_button_is_visible_when_i_write_text_to_edittext`() = runTest{
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("type" to ActionType.CREATE_NEW_NOTE, "selected_note_id" to 1)

        launchFragmentInHiltContainer<CreateNoteFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.edt_note)).perform(typeText("adda"), closeSoftKeyboard())

        onView(withId(R.id.saveBtn)).check(matches(isDisplayed()))
    }
}