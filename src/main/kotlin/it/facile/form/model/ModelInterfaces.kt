package it.facile.form.model

import it.facile.form.model.models.FieldModel
import it.facile.form.not
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle

/* ---------- Configurations ---------- */

abstract class FieldConfig(val label: String) : ViewModelGenerator, ViewModelStyleGenerator {}

interface FieldsContainer {
    fun fields(): List<FieldModel>
}

interface CustomPickerId {}

/* ---------- View Models ---------- */

interface ViewModelGenerator {
    fun getViewModel(key: String, storage: FormStorage): FieldViewModel
}

interface ViewModelStyleGenerator {
    fun getViewModelStyle(key: String, storage: FormStorage): FieldViewModelStyle
}

/* ---------- Rules ---------- */

interface FieldRulesValidator {
    val rules: (FormStorage) -> List<FieldRule>
    /** Return an error message if the value doesn't satisfy at least one rule, null otherwise */
    fun isValid(value: FieldValue, storage: FormStorage): String? {
        rules(storage).map {
            if (not(it.verify(value))) return it.errorMessage
        }
        return null
    }
}

abstract class FieldRule() {
    abstract val errorMessage: String
    /** Return if the value satisfies the rule and the error message to use if it doesn't */
    abstract fun verify(value: FieldValue): Boolean
    /** Return a list of objects containing keys to be observed to validate the rule */
    abstract fun observedKeys(): List<WithKey>
}

interface WithKey {
    val key: String
}

/** [FormStorage] reader that allows the client to only read the storage for a predefined key */
class KeyReader(override val key: String, private val storage: FormStorage) : WithKey {

    /** @see [FormStorage.getValue] */
    fun getValue(): FieldValue = storage.getValue(key)

    /** @see [FormStorage.isHidden] */
    fun isHidden(): Boolean = storage.isHidden(key)

    /** @see [FormStorage.getPossibleValues] */
    fun getPossibleValues(): FieldPossibleValues? = storage.getPossibleValues(key)
}

