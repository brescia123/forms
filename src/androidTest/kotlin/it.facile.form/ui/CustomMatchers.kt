package it.facile.form.ui

import android.support.design.widget.TextInputLayout
import android.view.View
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

fun hasTextInputLayoutHintText(expectedErrorText: String) =
        object : TypeSafeMatcher<View>() {

            override fun matchesSafely(view: View): Boolean {
                if (view !is TextInputLayout) {
                    return false
                }

                val error = view.hint ?: false
                val hint = error.toString()

                return expectedErrorText.equals(hint)
            }

            override fun describeTo(description: Description) {
            }
        }

