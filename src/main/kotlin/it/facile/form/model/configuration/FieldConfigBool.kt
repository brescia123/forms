package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldValue.Bool
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigBool(label: String,
                      val viewStyle: ViewStyle,
                      val boolToString: (Boolean) -> String) : FieldConfig(label) {

    enum class ViewStyle { CHECKBOX, TOGGLE }
    val defaultIfMissing = false

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle = when (value) {
        is Bool, Missing -> {
            val bool = (value as? Bool)?.bool ?: defaultIfMissing
            when (viewStyle) {
                ViewStyle.CHECKBOX -> FieldViewModelStyle.Checkbox(bool, boolToString(bool))
                ViewStyle.TOGGLE -> FieldViewModelStyle.Toggle(bool, boolToString(bool))
            }
        }
        else -> FieldViewModelStyle.InvalidType()
    }
}
