package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK

class FieldConfigurationBoolK(label: String,
                              val viewStyle: ViewStyle,
                              val boolToString: (Boolean) -> String) : FieldConfigurationK(label) {

    enum class ViewStyle { CHECKBOX, TOGGLE }

    override fun getViewModel(value: FieldValueK, hidden: Boolean): FieldViewModelK {
        return FieldViewModelK(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValueK): FieldViewModelStyleK = when (value) {
        is FieldValueK.Bool ->
            when (viewStyle) {
                ViewStyle.CHECKBOX -> FieldViewModelStyleK.Checkbox(value.bool, boolToString(value.bool))
                ViewStyle.TOGGLE -> FieldViewModelStyleK.Toggle(value.bool, boolToString(value.bool))
            }
        else -> FieldViewModelStyleK.InvalidType()
    }
}
