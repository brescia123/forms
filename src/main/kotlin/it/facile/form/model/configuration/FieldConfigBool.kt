package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigBool(label: String,
                      val viewStyle: ViewStyle,
                      val boolToString: (Boolean) -> String) : FieldConfig(label) {

    enum class ViewStyle { CHECKBOX, TOGGLE }

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle = when (value) {
        is FieldValue.Bool ->
            when (viewStyle) {
                ViewStyle.CHECKBOX -> FieldViewModelStyle.Checkbox(value.bool, boolToString(value.bool))
                ViewStyle.TOGGLE -> FieldViewModelStyle.Toggle(value.bool, boolToString(value.bool))
            }
        else -> FieldViewModelStyle.InvalidType()
    }
}
