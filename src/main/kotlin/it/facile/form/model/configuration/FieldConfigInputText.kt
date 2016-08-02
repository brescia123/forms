package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigInputText(label: String) : FieldConfig(label) {

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle = when (value) {
        is FieldValue.Text -> FieldViewModelStyle.InputText(value.text)
        else -> FieldViewModelStyle.InvalidType()
    }
}