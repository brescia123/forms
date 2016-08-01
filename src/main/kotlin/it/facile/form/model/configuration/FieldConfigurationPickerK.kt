package it.facile.form.model.configuration

import it.facile.form.viewmodel.DescribableK
import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK

class FieldConfigurationPickerK(val label: String,
                                val possibleValues: List<DescribableK>,
                                val placeHolder: String) : FieldConfigurationK {
    override fun label(): String {
        return label
    }

    override fun getViewModel(value: FieldValueK, hidden: Boolean): FieldViewModelK {
        return FieldViewModelK(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValueK): FieldViewModelStyleK =
            when (value) {
                is FieldValueK.Object -> FieldViewModelStyleK.Picker(
                        possibleValues,
                        value.value?.describe() ?: placeHolder)
                else -> FieldViewModelStyleK.InvalidType()
            }
}

