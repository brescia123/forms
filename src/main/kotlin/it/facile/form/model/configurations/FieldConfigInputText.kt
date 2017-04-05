package it.facile.form.model.configurations

import it.facile.form.model.*
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Text
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import it.facile.form.ui.viewmodel.FieldViewModelStyle.ExceptionText
import it.facile.form.ui.viewmodel.FieldViewModelStyle.InputText

class FieldConfigInputText(label: String,
                           override val rules: (FormStorageApi) -> List<FieldRule> = { emptyList() },
                           override val inputTextConfig: InputTextConfig = InputTextConfig(inputTextType = InputTextType.TEXT,
                                                                                            lines = 1,
                                                                                            maxLines = 1))
                            : FieldConfig(label), FieldRulesValidator, FieldInputMode {

    override fun getViewModel(key: String, storage: FormStorageApi): FieldViewModel {
        val value = storage.getValue(key)
        return FieldViewModel(
                label = label,
                style = getViewModelStyle(key, storage),
                hidden = storage.isHidden(key),
                disabled = storage.isDisabled(key),
                error = isValid(value, storage))
    }


    override fun getViewModelStyle(key: String, storage: FormStorageApi): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Text -> InputText(value.text, inputTextConfig)
            is Missing -> InputText("", inputTextConfig)
            else -> ExceptionText(FieldViewModelStyle.INVALID_TYPE)
        }
    }
}

