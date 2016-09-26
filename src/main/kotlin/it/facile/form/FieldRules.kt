package it.facile.form

import it.facile.form.model.FieldRule
import it.facile.form.model.KeyReader
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.Text

class NotMissing() : FieldRule() {
    override val errorMessage = "Field should not be empty"

    override fun verify(value: FieldValue) = when (value) {
        is FieldValue.Missing -> false
        is Text -> value.text.length > 0
        else -> true
    }

    override fun observedKeys() = emptyList<KeyReader>()
}

class IsEmail() : FieldRule() {
    val emailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")
    override val errorMessage = "Field should be a valid email"

    override fun verify(value: FieldValue) = value.asText()?.text?.matches(emailRegex) ?: false
    override fun observedKeys() = emptyList<KeyReader>()
}

class IsCellularPhone() : FieldRule() {
    val phoneETACSRegex = Regex("^3[0-9]{8}")
    val phoneGSMRegex = Regex("^3[0-9]{9}")
    override val errorMessage = "Field should be a valid phone number"
    override fun verify(value: FieldValue) =
            value.asText()?.text?.matchesAtLeastOne(phoneETACSRegex, phoneGSMRegex) ?: false

    override fun observedKeys() = emptyList<KeyReader>()
}

class ShouldBeTrue() : FieldRule() {
    override val errorMessage = "Field should be true"
    override fun verify(value: FieldValue) = value.asBool()?.bool ?: false
    override fun observedKeys() = emptyList<KeyReader>()
}

