package it.facile.form

import it.facile.form.model.FieldRule
import it.facile.form.model.KeyReader
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.Text

class NotMissing(override val errorMessage: String = "Field should not be empty") : FieldRule {
    override fun verify(value: FieldValue) = when (value) {
        is FieldValue.Missing -> false
        is Text -> value.text.isNotEmpty()
        else -> true
    }

    override fun observedKeys() = emptyList<KeyReader>()
}

class IsEmail(override val errorMessage: String = "Field should be a valid email") : FieldRule {
    val emailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")

    override fun verify(value: FieldValue) = value.asText()?.text?.matches(emailRegex) ?: false
    override fun observedKeys() = emptyList<KeyReader>()
}

class IsCellularPhone(override val errorMessage: String = "Field should be a valid phone number") : FieldRule {
    val phoneRegex = Regex("^3[0-9]{9}")
    override fun verify(value: FieldValue) =
            value.asText()?.text?.matches(phoneRegex) ?: false

    override fun observedKeys() = emptyList<KeyReader>()
}

class IsName(override val errorMessage: String = "Field should be a valid name") : FieldRule {
    val regex1 = Regex("(.)\\1\\1", RegexOption.IGNORE_CASE) // Three consecutive equal characters
    val regex2 = Regex("^[bcdfghlmnpqrstvzkxw]+$", RegexOption.IGNORE_CASE) // Only consonant ("j" is vocal)
    val regex3 = Regex("^[a-zòàùèéìíóáúäëïöü '\\-]+$", RegexOption.IGNORE_CASE)
    val regex4 = Regex("^[a-z]", RegexOption.IGNORE_CASE)

    override fun verify(value: FieldValue) =
            not(value.asText()?.text?.containsMatchOf(regex1) ?: false) and // There should not be three consecutive equal characters
            not(value.asText()?.text?.matches(regex2) ?: false) and // The string should not be composed only by consonant ("j" is vocal)
            (value.asText()?.text?.matches(regex3) ?: false) and // The string should contains only "a-zòàùèéìíóáúäëïöü "
            (value.asText()?.text?.containsMatchOf(regex4) ?: false)

    override fun observedKeys() = emptyList<KeyReader>()
}

class ShouldBeTrue(override val errorMessage: String = "Field should be true") : FieldRule {
    override fun verify(value: FieldValue) = value.asBool()?.bool ?: false
    override fun observedKeys() = emptyList<KeyReader>()
}

