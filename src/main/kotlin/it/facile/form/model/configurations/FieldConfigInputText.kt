package it.facile.form.model.configurations

import it.facile.form.model.*
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Text
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import it.facile.form.ui.viewmodel.FieldViewModelStyle.ExceptionText
import it.facile.form.ui.viewmodel.FieldViewModelStyle.InputText

class FieldConfigInputText(label: String,
                           override val rules: (FormStorage) -> List<FieldRule> = { emptyList() },
                           override val inputTextType: InputTextType = InputTextType.TEXT) : FieldConfig(label), FieldRulesValidator, FieldInputMode {

    override fun getViewModel(key: String, storage: FormStorage): FieldViewModel {
        val value = storage.getValue(key)
        return FieldViewModel(
                label,
                getViewModelStyle(key, storage),
                storage.isHidden(key),
                isValid(value, storage))
    }


    override fun getViewModelStyle(key: String, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Text -> InputText(value.text, inputTextType)
            is Missing -> InputText("", inputTextType)
            else -> ExceptionText(FieldViewModelStyle.INVALID_TYPE)
        }
    }
}

