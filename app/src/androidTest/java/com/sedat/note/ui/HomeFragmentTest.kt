package com.sedat.note.ui

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.sedat.note.R
import com.sedat.note.domain.model.ActionType
import com.sedat.note.domain.model.Note
import com.sedat.note.launchFragmentInHiltContainer
import com.sedat.note.presentation.homefragment.HomeFragment
import com.sedat.note.presentation.homefragment.HomeFragmentDirections
import com.sedat.note.presentation.homefragment.adapter.AdapterHomeFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito


@HiltAndroidTest
class HomeFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        hiltRule.inject()
    }

    @Test
    fun testNavigationFromHomeToCreateNote(){

        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())

        //navControl da homeFragment ten createNoteFragment e gittiğini doğrula
        Mockito.verify(navController).navigate(
            HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment()
        )
    }

    @Test
    fun uiItemsIsDisplayed(){
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<HomeFragment>() {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.root_layout_home_fragment)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.sub_linear_layout_home_fragment)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.search_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.text_input_layout)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.serachEdittext)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.search_icon)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.recylerNote)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.fab)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.backBtnForSubNotes)).check(
            matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE))
        )
    }

    @Test
    fun `when_i_click_recyclerview_item_navigate_homefragment_to_createnotefragment`(){
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(), navController)
            adapter.submitList(
                listOf(
                    Note(1, -1, "text", 1000, 0, 0)
                )
            )
        }

        Espresso.onView(ViewMatchers.withId(R.id.recylerNote)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AdapterHomeFragment.ViewHolder>(
                0,click()
            )
        )

        Mockito.verify(navController).navigate(
            HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(type = ActionType.UPDATE_NOTE, 1)
        )
    }

    @Test
    fun `navigate_homeFragmentToNoteImagesFragment_when_i_click_show_images_button_in_recyclerview_item`(){
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(), navController)
            adapter.submitList(
                listOf(
                    Note(2, -1, "text", 1000, 2, 2)
                )
            )
        }

        Espresso.onView(ViewMatchers.withId(R.id.recylerNote)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AdapterHomeFragment.ViewHolder>(
                0,
                object : ViewAction {
                    override fun getDescription(): String {
                        return "test"
                    }

                    override fun getConstraints(): Matcher<View> {
                        return isAssignableFrom(View::class.java)
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val imageView = view?.findViewById<ImageView>(R.id.btnImage)
                        imageView?.performClick()
                    }
                }
            )
        )

        //navControl da homeFragment ten createNoteFragment e gittiğini doğrula
        Mockito.verify(navController).navigate(
            HomeFragmentDirections.actionHomeFragmentToNoteImagesFragment(noteID = 2)
        )
    }

    @Test
    fun `navigate_actionHomeFragmentToCreateNoteFragment_when_i_click_recyclerview_item`(){
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<HomeFragment> {
            Navigation.setViewNavController(requireView(), navController)
            adapter.submitList(
                listOf(
                    Note(2, -1, "text", 1000, 2, 2)
                )
            )
        }

        Espresso.onView(ViewMatchers.withId(R.id.recylerNote)).perform(
            RecyclerViewActions.actionOnItemAtPosition<AdapterHomeFragment.ViewHolder>(
                0,
                object : ViewAction {
                    override fun getDescription(): String {
                        return "test"
                    }

                    override fun getConstraints(): Matcher<View> {
                        return isAssignableFrom(View::class.java)
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        val imageView = view?.findViewById<ConstraintLayout>(R.id.home_fragment_adapter_item_root_layout)
                        imageView?.performClick()
                    }
                }
            )
        )

        Mockito.verify(navController).navigate(
            HomeFragmentDirections.actionHomeFragmentToCreateNoteFragment(type = ActionType.UPDATE_NOTE, 2)
        )
    }
}