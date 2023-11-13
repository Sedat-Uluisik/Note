package com.sedat.note.ui

import android.view.View
import android.widget.ImageView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.registerIdlingResources
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.sedat.note.MainCoroutineRule
import com.sedat.note.R
import com.sedat.note.domain.model.ActionType
import com.sedat.note.domain.model.Note
import com.sedat.note.domain.model.NoteImage
import com.sedat.note.getOrAwaitValue
import com.sedat.note.launchFragmentInHiltContainer
import com.sedat.note.presentation.homefragment.HomeFragment
import com.sedat.note.presentation.homefragment.HomeFragmentDirections
import com.sedat.note.presentation.homefragment.adapter.AdapterHomeFragment
import com.sedat.note.presentation.noteimagesfragment.NoteImagesFragment
import com.sedat.note.presentation.noteimagesfragment.adapter.AdapterNoteImagesFragment
import com.sedat.note.presentation.noteimagesfragment.viewmodel.ViewModelNoteImages
import com.sedat.note.repo.FakeNoteRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@HiltAndroidTest
@ExperimentalCoroutinesApi
class NoteImagesFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule //işlemleri main thread da yapmak için kullanılıyor.
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule//işlemleri main thread da yapmak için kullanılıyor.
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup(){
        hiltRule.inject()
    }

    @Test
    fun `ui_items_visibility_testing`(){
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("noteID" to -2)

        launchFragmentInHiltContainer<NoteImagesFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.note_images_fragment_root_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.recycler_images)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.layout_zoomable)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )

        Espresso.onView(ViewMatchers.withId(R.id.img_zoom)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )

        Espresso.onView(ViewMatchers.withId(R.id.btn_close_image)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )
    }

    @Test
    fun `change_items_visibility_when_i_click_recyclerview_item`(){
        val navController = Mockito.mock(NavController::class.java)

        val args = bundleOf("noteID" to -2)

        launchFragmentInHiltContainer<NoteImagesFragment>(args) {
            Navigation.setViewNavController(requireView(), navController)
            adapter.submitList(
                listOf(
                   NoteImage(1, 1, "url", "desc"),
                   NoteImage(2, 1, "url2", "desc2")
                )
            )
        }

        Espresso.onView(ViewMatchers.withId(R.id.recycler_images)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AdapterNoteImagesFragment.ViewHolder>(
                0,
                object : ViewAction {
                    override fun getDescription(): String {
                        return "test"
                    }

                    override fun getConstraints(): Matcher<View> {
                        return ViewMatchers.isAssignableFrom(View::class.java)
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val layout = view?.findViewById<ConstraintLayout>(R.id.item_layout_images_root_layout)
                        layout?.performClick()
                    }
                }
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.note_images_fragment_root_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.recycler_images)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )

        Espresso.onView(ViewMatchers.withId(R.id.layout_zoomable)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        )

        Espresso.onView(ViewMatchers.withId(R.id.img_zoom)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        )

        Espresso.onView(ViewMatchers.withId(R.id.btn_close_image)).check(
            ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
        )
    }
}