package it.facile.form.model.configuration

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

/* ---------- Configurations ---------- */

abstract class FieldConfig(val label: String) : ViewModelGenerator, ViewModelStyleGenerator {}

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
    fun isValid(value: FieldValue): String? {
        rules.map {
            val verify = it.verify(value)
            if (!verify.first) return verify.second
        }
        return null
    }
}

interface FieldRule {
    /** Return if the value satisfies the rule and the error message to use if it doesn't */
    fun verify(value: FieldValue): Pair<Boolean, String>
}