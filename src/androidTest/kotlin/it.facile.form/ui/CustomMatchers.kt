package it.facile.form.ui

import android.support.design.widget.TextInputLayout
import android.view.View
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

fun hasTextInputLayoutHintText(expectedText: String) =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("View with hint text: " + expectedText)
            }

            override fun matchesSafely(view: View) = (view as? TextInputLayout)?.hint.toString() == expectedText
        }
