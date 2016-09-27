package it.facile.form

import it.facile.form.model.FieldRule
import it.facile.form.model.KeyReader
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.Text

class NotMissing(override val errorMessage: String = "Field should not be empty") : FieldRule() {
    override fun verify(value: FieldValue) = when (value) {
        is FieldValue.Missing -> false
        is Text -> value.text.length > 0
        else -> true
    }

    override fun observedKeys() = emptyList<KeyReader>()
}

class IsEmail(override val errorMessage: String = "Field should be a valid email") : FieldRule() {
    val emailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")

    override fun verify(value: FieldValue) = value.asText()?.text?.matches(emailRegex) ?: false
    override fun observedKeys() = emptyList<KeyReader>()
}

class IsCellularPhone(override val errorMessage: String = "Field should be a valid phone number") : FieldRule() {
    val phoneETACSRegex = Regex("^3[0-9]{8}")
    val phoneGSMRegex = Regex("^3[0-9]{9}")
    override fun verify(value: FieldValue) =
            value.asText()?.text?.matchesAtLeastOne(phoneETACSRegex, phoneGSMRegex) ?: false

    override fun observedKeys() = emptyList<KeyReader>()
}

class ShouldBeTrue(override val errorMessage: String = "Field should be true") : FieldRule() {
    override fun verify(value: FieldValue) = value.asBool()?.bool ?: false
    override fun observedKeys() = emptyList<KeyReader>()
}

