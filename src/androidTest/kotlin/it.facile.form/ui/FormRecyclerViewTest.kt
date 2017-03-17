package it.facile.form.ui

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import it.facile.form.R
import it.facile.form.SectionedRecyclerViewAdapter
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.view.WindowManager
import org.junit.Before




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
    fun Should_Show_Form_Errors_If_Presents() {
        onView(withId(R.id.showErrorsCheckBox))
                .perform(click())

        //Picker
        onView(allOf(withParent(withChild(withText("Utenza"))), withId(R.id.textErrorImage)))
                .check(matches(isDisplayed()))

        //Toggle
        onView(allOf(withParent(withChild(withText("Nuova fornitura"))), withId(R.id.toggleErrorImage)))
                .check(matches(isDisplayed()))

        //CheckBox
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
    fun Should_Open_Dialog_On_Click_Picker_Field() {
        onView(withText("Attuale fornitore"))
                .perform(click())
        onView(withClassName(containsString("Dialog")))
                .check(matches(isDisplayed()))
    }

    @Test
    fun Should_Check_Toggle_On_Click_Toggle_Field() {
        onView(withClassName(containsString("SwitchCompat")))
                .perform(click())
                .check(matches(isChecked()))
    }

    @Test
    fun Should_Uncheck_Toggle_On_Double_Click_Toggle_Field() {
        onView(withClassName(containsString("SwitchCompat")))
                .perform(click())
                .perform(click())
                .check(matches(isNotChecked()))
    }

    @Test
    fun Should_Check_Checkbox_On_Click_Checkbox_Field() {
        onView(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))
                .perform(click())
                .check(matches(isChecked()))
    }

    @Test
    fun Should_Uncheck_Checkbox_On_Double_Click_Checkbox_Field() {
        onView(allOf(withId(R.id.checkboxView), withParent(withChild(withText(containsString("Cucina"))))))
                .perform(click())
                .perform(click())
                .check(matches(not(isChecked())))
    }

    @Test
    fun Should_Display_Field_After_Visibility_Changed() {
        onView(withText("Utenza")).perform(click())
        onView(withText("Business")).perform(click())

        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(hasTextInputLayoutHintText("Ragione Sociale"))))
        onView(hasTextInputLayoutHintText("Ragione Sociale"))
                .check(matches(isDisplayed()))
    }

    @Test
    fun Should_Not_Display_Field_After_Visibility_Changed() {
        onView(withText("Utenza")).perform(click())
        onView(withText("Business")).perform(click())

        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(hasTextInputLayoutHintText("Cognome"))))

        onView(hasTextInputLayoutHintText("Cognome"))
                .check(matches(not(isDisplayed())))
    }

    @Test
    fun Should_Not_Display_Section_Title_If_All_Fields_Are_Hidden() {
        onView(withText("Utenza")).perform(click())
        onView(withText("Business")).perform(click())

        onView(withId(R.id.singlePageFormRecyclerView))
                .perform(scrollTo<SectionedRecyclerViewAdapter.SectionViewHolder>
                (hasDescendant(withText("Utilizzo del gas"))))

        onView(withText("Utilizzo del gas"))
                .check(matches(not(isDisplayed())))
    }
}


