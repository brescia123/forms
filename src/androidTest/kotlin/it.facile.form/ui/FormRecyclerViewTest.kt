package it.facile.form.ui

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollTo
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
    val mActivityRule = ActivityTestRule(
            MainActivityTest::class.java)


    @Before
    fun unlockScreen() {
        val activity = mActivityRule.activity
        val wakeUpDevice = Runnable {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        activity.runOnUiThread(wakeUpDevice)
    }

    @Test
    fun anyFieldViewsInErrorState_showErrorImage_whenRequired() {
        onView(withId(R.id.showErrorsCheckBox))
                .perform(click())

        //Picker
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.textView), withParent(withChild(withText("Utenza")))))))
        onView(allOf(withParent(withChild(withText("Utenza"))), withId(R.id.textErrorImage)))
                .check(matches(isDisplayed()))

        //Toggle
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.toggleView), withParent(withChild(withText("Nuova fornitura")))))))
        onView(allOf(withParent(withChild(withText("Nuova fornitura"))), withId(R.id.toggleErrorImage)))
                .check(matches(isDisplayed()))

        //CheckBox
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))))
        onView(allOf(withParent(withChild(withText("Cucina"))), withId(R.id.checkboxErrorImage)))
                .check(matches(isDisplayed()))

        //Input
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(hasTextInputLayoutHintText("Nome"))))
        onView(withText(containsString("seleziona il campoNome")))
                .check(matches(isDisplayed()))
    }

    @Test
    fun pickerField_openDialog_whenClicked() {
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.textView), withParent(withChild(withText("Attuale fornitore")))))))
        onView(withText("Attuale fornitore"))
                .perform(click())
        onView(withClassName(containsString("Dialog")))
                .check(matches(isDisplayed()))
    }

    @Test
    fun toggleField_becomeChecked_whenClicked() {
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.toggleView), withClassName(containsString("SwitchCompat"))))))
        onView(withClassName(containsString("SwitchCompat")))
                .perform(click())
                .check(matches(isChecked()))
    }

    @Test
    fun toggleField_becomeUnchecked_whenDoubleClicked() {
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.toggleView), withClassName(containsString("SwitchCompat"))))))
        onView(withClassName(containsString("SwitchCompat")))
                .perform(click())
                .perform(click())
                .check(matches(isNotChecked()))
    }

    @Test
    fun checkboxField_becomeChecked_whenClicked() {
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))))

        onView(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))
                .perform(click())
                .check(matches(isChecked()))
    }

    @Test
    fun checkboxField_becomeUnchecked_whenDoubleClicked() {
        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))))

        onView(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))
                .perform(click())
                .perform(click())
                .check(matches(not(isChecked())))
    }

    @Test
    fun fieldView_mustBeDisplayed_whenVisibilityChangeToTrue() {
        onView(withText("Utenza")).perform(click())
        onView(withText("Business")).perform(click())

        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(hasTextInputLayoutHintText("Ragione Sociale"))))
        onView(hasTextInputLayoutHintText("Ragione Sociale"))
                .check(matches(isDisplayed()))
    }

    @Test
    fun fieldView_mustNotBeDisplayed_whenVisibilityChangeToFalse() {
        onView(withText("Utenza")).perform(click())
        onView(withText("Business")).perform(click())

        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(hasTextInputLayoutHintText("Cognome"))))

        onView(hasTextInputLayoutHintText("Cognome"))
                .check(matches(not(isDisplayed())))
    }

    @Test
    fun sectionTitle_mustNotBeDisplayed_whenAllFieldsAreHidden() {
        onView(withText("Utenza")).perform(click())
        onView(withText("Business")).perform(click())

        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(withText("Utilizzo del gas"))))

        onView(withText("Utilizzo del gas"))
                .check(matches(not(isDisplayed())))
    }
}


