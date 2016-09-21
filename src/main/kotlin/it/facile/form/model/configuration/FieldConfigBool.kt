package it.facile.form.model.configuration

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldValue.Bool
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

class FieldConfigBool(label: String,
                      val viewStyle: ViewStyle,
                      val boolToString: ((Boolean) -> String) = { "" }) : FieldConfig(label) {

    enum class ViewStyle { CHECKBOX, TOGGLE }

    val defaultIfMissing = false

    override fun getViewModel(key: Int, storage: FormStorage): FieldViewModel {
        return FieldViewModel(
                label,
                getViewModelStyle(key, storage),
                storage.isHidden(key),
                null)
    }

    override fun getViewModelStyle(key: Int, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Bool -> chooseViewModelStyle(value.bool)
            is Missing -> chooseViewModelStyle(defaultIfMissing)
            else -> FieldViewModelStyle.InvalidType()
        }
    }

    private fun chooseViewModelStyle(bool: Boolean) = when (viewStyle) {
        ViewStyle.CHECKBOX -> FieldViewModelStyle.Checkbox(bool, boolToString(bool))
        ViewStyle.TOGGLE -> FieldViewModelStyle.Toggle(bool, boolToString(bool))
    }
}
