package it.facile.form.model.configurations

import it.facile.form.model.FieldConfig
import it.facile.form.model.FieldRule
import it.facile.form.model.FieldRulesValidator
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldPossibleValues.*
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Object
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import it.facile.form.ui.viewmodel.FieldViewModelStyle.*
import rx.Subscription

class FieldConfigPicker(label: String,
                        val possibleValues: FieldPossibleValues,
                        val placeHolder: String = "Select a value",
                        override val rules: (FormStorage) -> List<FieldRule> = { emptyList() }) : FieldConfig(label), FieldRulesValidator {

    var sub: Subscription? = null

    override fun getViewModel(key: String, storage: FormStorage) =
            FieldViewModel(
                    label,
                    getViewModelStyle(key, storage),
                    storage.isHidden(key),
                    isValid(storage.getValue(key), storage))


    val possibleValuesGenerator: (FormStorage, String) -> FieldPossibleValues = { storage, key -> storage.getPossibleValues(key) ?: possibleValues }

    override fun getViewModelStyle(key: String, storage: FormStorage): FieldViewModelStyle {
        val value = storage.getValue(key)
        return when (value) {
            is Object -> chooseViewModelStyle(storage, key, value.value.textDescription)
            is Missing -> chooseViewModelStyle(storage, key, placeHolder)
            else -> ExceptionText(FieldViewModelStyle.INVALID_TYPE)
        }
    }

    private fun chooseViewModelStyle(storage: FormStorage, key: String, text: String): FieldViewModelStyle {
        sub?.unsubscribe()
        val possibleValues = possibleValuesGenerator(storage, key)
        return when (possibleValues) {
            is Available -> Picker(possibleValues.list, text)
            is ToBeRetrieved -> {
                sub = possibleValues.retrieve()
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { storage.putPossibleValues(key, Available(it)) },
                                { storage.putPossibleValues(key, RetrieveError(it.message ?: "Generic error")) }
                        )
                Loading()
            }
            is RetrieveError -> ExceptionText(possibleValues.errorMessage)
        }
    }
}