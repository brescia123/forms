package it.facile.form.model.configuration

import it.facile.form.matchesAtLeastOne
import it.facile.form.viewmodel.FieldValue

private const val MISSING_ERROR_MESSAGE = "Field should not be empty"

class NotMissing : FieldRule {
    override fun verify(value: FieldValue): Pair<Boolean, String> = when (value) {
        is FieldValue.Missing -> false to MISSING_ERROR_MESSAGE
        is FieldValue.Text -> (value.text.length > 0) to MISSING_ERROR_MESSAGE
        else -> true to MISSING_ERROR_MESSAGE
    }
}

class IsEmail : FieldRule {
    val emailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")


    override fun verify(value: FieldValue): Pair<Boolean, String> =
            if (value !is FieldValue.Text) false to
                    "Invalid value for IsEmail rule"
            else value.text.matches(emailRegex) to
                    "Field should be a valid email"

}

class IsPhone : FieldRule {
    val phoneETACSRegex = Regex("^3[0-9]{8}")
    val phoneGSMRegex = Regex("^3[0-9]{9}")

    override fun verify(value: FieldValue): Pair<Boolean, String> =
            if (value !is FieldValue.Text) false to
                    "Invalid value for IsEmail rule"
            else value.text.matchesAtLeastOne(phoneGSMRegex, phoneETACSRegex) to
                    "Field should be a valid phone number"
}