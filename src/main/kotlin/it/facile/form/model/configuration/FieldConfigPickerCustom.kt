package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Object
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigPickerCustom(label: String,
                              val id: CustomPickerId,
                              val placeHolder: String = "Select a value",
                              val rules: List<FieldRule> = emptyList()) : FieldConfig(label), FieldRulesValidator {

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, isValid(rules, value))
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle = when (value) {
        is Object, Missing -> {
            FieldViewModelStyle.CustomPicker(
                    identifier = id,
                    valueText = (value as? Object)?.value?.describe() ?: placeHolder)
        }
        else -> FieldViewModelStyle.InvalidType()
    }
}