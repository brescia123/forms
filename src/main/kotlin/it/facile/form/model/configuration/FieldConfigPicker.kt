package it.facile.form.model.configuration

import it.facile.form.FormStorage
import it.facile.form.viewmodel.DescribableWithKey
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Object
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import it.facile.form.viewmodel.FieldViewModelStyle.Loading
import it.facile.form.viewmodel.FieldViewModelStyle.Picker
import rx.Completable

class FieldConfigPicker(label: String,
                        val possibleValuesGenerator: (FormStorage, Int) -> List<DescribableWithKey>,
                        val placeHolder: String = "Select a value",
                        override val rules: List<FieldRule> = emptyList()) : FieldConfig(label), FieldRulesValidator, DeferredConfig {

    override fun getViewModel(key: Int, storage: FormStorage) =
            FieldViewModel(
                    label,
                    getViewModelStyle(key, storage),
                    storage.isHidden(key),
                    isValid(storage.getValue(key)))

    override fun getViewModelStyle(key: Int, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        val possibleValues = possibleValuesGenerator(storage, key)
        return when (value) {
            is Object -> chooseViewModelStyle(possibleValues, value.value.describe())
            is Missing -> chooseViewModelStyle(possibleValues, placeHolder)
            else -> FieldViewModelStyle.InvalidType()
        }
    }

    override fun observe(): Completable {
        return Completable.complete()
    }

    private fun chooseViewModelStyle(possibleValues: List<DescribableWithKey>?, text: String) =
            if (possibleValues != null) Picker(possibleValues, text) else Loading()
}

