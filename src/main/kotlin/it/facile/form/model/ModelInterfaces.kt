package it.facile.form.model

import it.facile.form.model.models.FieldModel
import it.facile.form.not
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle

/* ---------- Configurations ---------- */

interface FieldConfigApi : ViewModelGenerator, ViewModelStyleGenerator {
    val label: String
}
abstract class FieldConfig(override val label: String) : FieldConfigApi

interface FieldsContainer {
    fun fields(): List<FieldModel>
}

interface CouldHaveLoadingError {
    val errorMessage: String
    var hasErrors: Boolean
}

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
        rules(storage).forEach { if (not(it.verify(value))) return it.errorMessage }
        return null
    }
}

interface FieldRule {
    val errorMessage: String
    /** Return if the value satisfies the rule and the error message to use if it doesn't */
    fun verify(value: FieldValue): Boolean

    /** Return a list of objects containing keys to be observed to validate the rule */
    fun observedKeys(): List<WithKey>
}

interface FieldInputMode {
    val inputTextType: InputTextType
}

enum class InputTextType {
    TEXT,
    CAP_WORDS,
    EMAIL,
    PHONE,
    NUMBER
}

interface WithKey {
    val key: String
}

/** [FormStorage] reader that allows the client to only read the storage for a predefined key */
class KeyReader(override val key: String, private val storage: FormStorageApi) : WithKey {

    /** @see [FormStorage.getValue] */
    fun getValue(): FieldValue = storage.getValue(key)

    /** @see [FormStorage.isHidden] */
    fun isHidden(): Boolean = storage.isHidden(key)

    /** @see [FormStorage.getPossibleValues] */
    fun getPossibleValues(): FieldPossibleValues? = storage.getPossibleValues(key)

    /** @see [FormStorage.isDisabled] */
    fun isDisabled(): Boolean = storage.isDisabled(key)
}
