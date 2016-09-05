package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import rx.Single

/* ---------- Configurations ---------- */

abstract class FieldConfig(val label: String) : ViewModelGenerator, ViewModelStyleGenerator {}

interface DeferredConfig {
    fun observe(): Single<Unit>
}

/* ---------- View Models ---------- */

interface ViewModelGenerator {
    fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel
}

interface ViewModelStyleGenerator {
    fun getViewModelStyle(value: FieldValue): FieldViewModelStyle
}

/* ---------- Rules ---------- */

interface FieldRulesValidator {
    /** Return an error message if the value doesn't satisfy at least one rule, null otherwise */
    fun isValid(rules: List<FieldRule>, value: FieldValue): String? {
        rules.map {
            val verify = it.verify(value)
            if (!verify.first) return verify.second
        }
        return null
    }
}

interface FieldRule {
    /** Return a if the value satisfies the rule and the error message to use if it doesn't */
    fun verify(value: FieldValue): Pair<Boolean, String>
}