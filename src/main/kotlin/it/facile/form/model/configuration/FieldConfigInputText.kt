package it.facile.form.model.configuration

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Text
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigInputText(label: String,
                           override val rules: List<FieldRule> = emptyList()) : FieldConfig(label), FieldRulesValidator {

    override fun getViewModel(key: Int, storage: FormStorage): FieldViewModel {
        val value = storage.getValue(key)
        return FieldViewModel(
                label,
                getViewModelStyle(key, storage),
                storage.isHidden(key),
                isValid(value))
    }


    override fun getViewModelStyle(key: Int, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Text -> FieldViewModelStyle.InputText(value.text)
            is Missing -> FieldViewModelStyle.InputText("")
            else -> FieldViewModelStyle.InvalidType()
        }
    }
}