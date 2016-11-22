package it.facile.form.model.configurations

import it.facile.form.model.FieldConfig
import it.facile.form.model.FieldRule
import it.facile.form.model.FieldRulesValidator
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Object
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import it.facile.form.ui.viewmodel.FieldViewModelStyle.CustomPicker
import it.facile.form.ui.viewmodel.FieldViewModelStyle.ExceptionText

class FieldConfigPickerCustom(label: String,
                              val id: String,
                              val placeHolder: String = "Select a value",
                              override val rules: (FormStorage) -> List<FieldRule> = { emptyList() }) : FieldConfig(label), FieldRulesValidator {

    override fun getViewModel(key: String, storage: FormStorage): FieldViewModel {
        val value = storage.getValue(key)
        return FieldViewModel(
                label = label,
                style = getViewModelStyle(key, storage),
                hidden = storage.isHidden(key),
                disabled = storage.isDisabled(key),
                error = isValid(value, storage))
    }

    override fun getViewModelStyle(key: String, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Object -> CustomPicker(identifier = id, valueText = value.value.textDescription)
            is Missing -> CustomPicker(identifier = id, valueText = placeHolder)
            else -> ExceptionText(FieldViewModelStyle.INVALID_TYPE)
        }
    }
}