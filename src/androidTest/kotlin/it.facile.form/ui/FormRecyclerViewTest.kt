package it.facile.form.ui

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.WindowManager
import it.facile.form.R
import it.facile.form.SectionedRecyclerViewAdapter
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FormRecyclerViewTest {

    @Rule @JvmField
    val mActivityRule = ActivityTestRule(MainActivityTest::class.java, true, false)

    @Before
    fun unlockScreen() {
        mActivityRule.launchActivity(null)
        val activity = mActivityRule.activity
        activity.runOnUiThread {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }


    }

    @Test
    fun fieldPicker_showErrorImage_whenInErrorState() {
        val errorImageFieldPickerMatcher = allOf(withParent(withChild(withText("FieldPicker (Position 1)"))), withId(R.id.textErrorImage))

        onView(withId(R.id.showErrorsCheckBox)).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(1))

        onView(errorImageFieldPickerMatcher).check(matches(isDisplayed()))
    }

    @Test
    fun fieldToggle_showErrorImage_whenInErrorState() {
        val errorImageFieldToggleMatcher = allOf(withParent(withChild(withText("FieldToggle (Position 2)"))), withId(R.id.toggleErrorImage))

        onView(withId(R.id.showErrorsCheckBox)).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(2))

        onView(errorImageFieldToggleMatcher).check(matches(isDisplayed()))
    }

    @Test
    fun fieldCheckbox_showErrorImage_whenInErrorState() {
        val errorImageFieldCheckboxMatcher = allOf(withParent(withChild(withText("Checkbox (Position 7)"))), withId(R.id.checkboxErrorImage))

        onView(withId(R.id.showErrorsCheckBox)).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(7))

        onView(errorImageFieldCheckboxMatcher).check(matches(isDisplayed()))
    }

    @Test
    fun fieldInput_showErrorImage_whenInErrorState() {
        val errorTextFieldInputMatcher = withText(containsString("Input (Position 14)"))

        onView(withId(R.id.showErrorsCheckBox)).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(14))

        onView(errorTextFieldInputMatcher).check(matches(isDisplayed()))
    }

    @Test
    fun fieldPicker_openDialog_whenClicked() {
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(3))

        onView(withText("FieldPicker (Position 3)"))
                .perform(click())
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))
    }

    @Test
    fun fieldToggle_becomeChecked_whenClicked() {
        val fieldToggleMatcher = allOf(withId(R.id.toggleView), withParent(withChild(withText("FieldToggle (Position 2)"))))
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(2))

        onView(fieldToggleMatcher)
                .perform(click())
                .check(matches(isChecked()))
    }

    @Test
    fun fieldToggle_becomeUnchecked_whenDoubleClicked() {
        val fieldToggleMatcher = allOf(withId(R.id.toggleView), withParent(withChild(withText("FieldToggle (Position 2)"))))
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(2))

        onView(fieldToggleMatcher)
                .perform(click())
                .perform(click())
                .check(matches(isNotChecked()))
    }

    @Test
    fun fieldCheckbox_becomeChecked_whenClicked() {
        val fieldCheckboxMatcher = allOf(withId(R.id.checkboxView), withParent(withChild(withText("Checkbox (Position 7)"))))
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(6))

        onView(fieldCheckboxMatcher)
                .perform(click())
                .check(matches(isChecked()))
    }

    @Test
    fun fieldCheckbox_becomeUnchecked_whenDoubleClicked() {
        val fieldCheckboxMatcher = allOf(withId(R.id.checkboxView), withParent(withChild(withText("Checkbox (Position 7)"))))
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(6))

        onView(fieldCheckboxMatcher)
                .perform(click())
                .perform(click())
                .check(matches(not(isChecked())))
    }

    @Test
    fun fieldView_shouldBeDisplayed_whenVisibilityChangeToTrue() {
        onView(withId(R.id.singlePageFormRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(1, click()))
        onView(withText("Group 2")).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(17))

        onView(hasTextInputLayoutHintText("Input (Position 17)")).check(matches(isDisplayed()))
    }

    @Test
    fun fieldView_shouldNotBeDisplayed_whenVisibilityChangeToFalse() {
        onView(withId(R.id.singlePageFormRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(1, click()))
        onView(withText("Group 2")).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(14))

        onView(hasTextInputLayoutHintText("Input (Position 14)")).check(matches(not(isDisplayed())))
    }

    @Test
    fun sectionTitle_shouldNotBeDisplayed_whenAllItsFieldsAreHidden() {
        onView(withId(R.id.singlePageFormRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(1, click()))
        onView(withText("Group 2")).perform(click())
        onView(withId(R.id.singlePageFormRecyclerView)).perform(scrollToPosition<SectionedRecyclerViewAdapter.SectionViewHolder>(10))

        onView(withText("Section2 (Position 10)")).check(matches(not(isDisplayed())))
    }
}


