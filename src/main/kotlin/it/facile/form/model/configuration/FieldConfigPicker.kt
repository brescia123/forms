package it.facile.form.model.configuration

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldPossibleValues
import it.facile.form.viewmodel.FieldPossibleValues.*
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Object
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import it.facile.form.viewmodel.FieldViewModelStyle.*
import rx.Subscription

class FieldConfigPicker(label: String,
                        val possibleValuesGenerator: (FormStorage, Int) -> FieldPossibleValues,
                        val placeHolder: String = "Select a value",
                        override val rules: List<FieldRule> = emptyList()) : FieldConfig(label), FieldRulesValidator {

    var sub: Subscription? = null

    override fun getViewModel(key: Int, storage: FormStorage) =
            FieldViewModel(
                    label,
                    getViewModelStyle(key, storage),
                    storage.isHidden(key),
                    isValid(storage.getValue(key)))

    override fun getViewModelStyle(key: Int, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Object -> chooseViewModelStyle(storage, key, value.value.describe())
            is Missing -> chooseViewModelStyle(storage, key, placeHolder)
            else -> ExceptionText(FieldViewModelStyle.INVALID_TYPE)
        }
    }

    private fun chooseViewModelStyle(storage: FormStorage, key: Int, text: String): FieldViewModelStyle {
        sub?.unsubscribe()
        val possibleValues = possibleValuesGenerator(storage, key)
        return when (possibleValues) {
            is Available -> Picker(possibleValues.list, text)
            is ToBeRetrieved -> {
                sub = possibleValues.retrieve().subscribe(
                        { storage.putPossibleValues(key, Available(it)) },
                        { storage.putPossibleValues(key, RetrieveError(it.message ?: "Generic error")) }
                )
                Loading()
            }
            is RetrieveError -> ExceptionText(possibleValues.errorMessage)
        }
    }
}