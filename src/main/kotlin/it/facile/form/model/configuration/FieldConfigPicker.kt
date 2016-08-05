package it.facile.form.model.configuration

import it.facile.form.viewmodel.DescribableWithKey
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import rx.Single

class FieldConfigPicker(label: String,
                        val possibleValuesSingle: Single<List<DescribableWithKey>>,
                        val placeHolder: String) : FieldConfig(label), DeferredConfig {
    var possibleValues: List<DescribableWithKey>? = null

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle =
            when (value) {
                is FieldValue.Object ->
                    if (possibleValues != null)
                        FieldViewModelStyle.Picker(
                                possibleValues as List<DescribableWithKey>,
                                value.value?.describe() ?: placeHolder)
                    else FieldViewModelStyle.Loading()
                else -> FieldViewModelStyle.InvalidType()
            }

    override fun observe(): Single<Unit> {
        return possibleValuesSingle.doOnSuccess {
            possibleValues = it
        }.flatMap { Single.just<Unit>(null) }
    }
}

