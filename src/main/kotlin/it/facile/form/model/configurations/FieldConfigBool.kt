package it.facile.form.model.configurations

import it.facile.form.model.FieldConfig
import it.facile.form.model.FieldRule
import it.facile.form.model.FieldRulesValidator
import it.facile.form.storage.FieldValue.Bool
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import it.facile.form.ui.viewmodel.FieldViewModelStyle.*

class FieldConfigBool(label: String,
                      val viewStyle: ViewStyle,
                      val boolToString: ((Boolean) -> String) = { "" },
                      override val rules: (FormStorage) -> List<FieldRule> = { emptyList() }) : FieldConfig(label), FieldRulesValidator {

    enum class ViewStyle { CHECKBOX, TOGGLE }

    val defaultIfMissing = false

    override fun getViewModel(key: String, storage: FormStorage): FieldViewModel {
        return FieldViewModel(
                label,
                getViewModelStyle(key, storage),
                storage.isHidden(key),
                isValid(storage.getValue(key), storage))
    }

    override fun getViewModelStyle(key: String, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Bool -> chooseViewModelStyle(value.bool)
            is Missing -> chooseViewModelStyle(defaultIfMissing)
            else -> ExceptionText(FieldViewModelStyle.INVALID_TYPE)
        }
    }

    private fun chooseViewModelStyle(bool: Boolean) = when (viewStyle) {
        ViewStyle.CHECKBOX -> Checkbox(bool, boolToString(bool))
        ViewStyle.TOGGLE -> Toggle(bool, boolToString(bool))
    }
}
