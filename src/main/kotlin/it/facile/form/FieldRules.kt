package it.facile.form

import it.facile.form.model.FieldRule
import it.facile.form.model.StorageActionExecutor
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.Text

class NotMissing(key: Int? = null) : FieldRule(key) {
    override val errorMessage = "Field should not be empty"

    override fun verify(value: FieldValue, executor: StorageActionExecutor?)= when (value) {
        is FieldValue.Missing -> false
        is Text -> value.text.length > 0
        else -> true
    }
}

class IsEmail(key: Int? = null) : FieldRule(key) {
    val emailRegex = Regex("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")
    override val errorMessage = "Field should be a valid email"

    override fun verify(value: FieldValue, executor: StorageActionExecutor?)= when (value) {
        is Text -> value.text.matches(emailRegex)
        else -> false
    }
}

class IsPhone(key: Int? = null) : FieldRule(key) {
    val phoneETACSRegex = Regex("^3[0-9]{8}")
    val phoneGSMRegex = Regex("^3[0-9]{9}")
    override val errorMessage = "Field should be a valid phone number"

    override fun verify(value: FieldValue, executor: StorageActionExecutor?)= when (value) {
        is Text -> value.text.matchesAtLeastOne(phoneGSMRegex, phoneETACSRegex)
        else -> false
    }
}