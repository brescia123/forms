package it.facile.form.model.configuration

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Object
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import it.facile.form.viewmodel.FieldViewModelStyle.CustomPicker

class FieldConfigPickerCustom(label: String,
                              val id: CustomPickerId,
                              val placeHolder: String = "Select a value",
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
            is Object -> CustomPicker(identifier = id, valueText = value.value.describe())
            is Missing -> CustomPicker(identifier = id, valueText = placeHolder)
            else -> FieldViewModelStyle.InvalidType()
        }
    }
}