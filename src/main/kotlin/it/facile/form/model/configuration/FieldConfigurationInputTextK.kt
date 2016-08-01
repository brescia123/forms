package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK

class FieldConfigurationInputTextK(val label: String) : FieldConfigurationK {
    override fun label(): String {
        return label
    }

    override fun getViewModel(value: FieldValueK, hidden: Boolean): FieldViewModelK {
        return FieldViewModelK(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValueK): FieldViewModelStyleK = when (value) {
        is FieldValueK.Text -> FieldViewModelStyleK.InputText(value.text)
        else -> FieldViewModelStyleK.InvalidType()
    }
}