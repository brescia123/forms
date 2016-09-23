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

/* ---------- View Models ---------- */
interface ViewModelGenerator {
    fun getViewModel(key: Int, storage: FormStorage): FieldViewModel
}

interface ViewModelStyleGenerator {
    fun getViewModelStyle(key: Int, storage: FormStorage): FieldViewModelStyle
}

/* ---------- Rules ---------- */

interface FieldRulesValidator {
    val rules: List<FieldRule>
    /** Return an error message if the value doesn't satisfy at least one rule, null otherwise */
    fun isValid(value: FieldValue, storage: FormStorage): String? {
        rules.map { if (not(it.verify(value, it.key?.storageActionExecutor(storage)))) return it.errorMessage }
        return null
    }
}

abstract class FieldRule(val key: Int? = null) {
    abstract val errorMessage: String
    /** Return if the value satisfies the rule and the error message to use if it doesn't */
    abstract fun verify(value: FieldValue, executor: StorageActionExecutor?): Boolean
    fun hasObservedKey() = key != null
}

interface CustomPickerId {
}

fun Int.storageActionExecutor(storage: FormStorage) = StorageActionExecutor(this, storage)

class StorageActionExecutor(val key: Int, val storage: FormStorage) {

    /** @see [FormStorage.getValue] */
    fun getValue(): FieldValue = storage.getValue(key)

    /** @see [FormStorage.isHidden] */
    fun isHidden(): Boolean = storage.isHidden(key)

    /** @see [FormStorage.getPossibleValues] */
    fun getPossibleValues(): FieldPossibleValues? = storage.getPossibleValues(key)

    /** @see [FormStorage.putValue] */
    fun putValue(value: FieldValue) {
        storage.putValue(key, value)
    }

    /** @see [FormStorage.setVisibility] */
    fun setVisibility(hidden: Boolean) {
        storage.setVisibility(key, hidden)
    }

    /** @see [FormStorage.putPossibleValues] */
    fun putPossibleValues(possibleValues: FieldPossibleValues) {
        storage.putPossibleValues(key, possibleValues)
    }

    /** @see [FormStorage.switchPossibleValues] */
    fun switchPossibleValues(possibleValues: FieldPossibleValues) {
        storage.switchPossibleValues(key, possibleValues)
    }

    /** @see [FormStorage.putValueAndSetVisibility] */
    fun putValueAndSetVisibility(value: FieldValue, hidden: Boolean) {
        storage.putValueAndSetVisibility(key, value, hidden)
    }

    /** @see [FormStorage.clearValue] */
    fun clearValue() {
        storage.clearValue(key)
    }

    /** @see [FormStorage.clearPossibleValues] */
    fun clearPossibleValues() {
        storage.clearPossibleValues(key)
    }
}