package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Text
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigInputText(label: String,
                           val rules: List<FieldRule> = emptyList()) : FieldConfig(label), FieldRulesValidator {

    val defaultTextIfMissing = ""

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, isValid(rules, value))
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle = when (value) {
        is Text, Missing -> {
            val text = (value as? Text)?.text ?: defaultTextIfMissing
            FieldViewModelStyle.InputText(text)
        }
        else -> FieldViewModelStyle.InvalidType()
    }
}