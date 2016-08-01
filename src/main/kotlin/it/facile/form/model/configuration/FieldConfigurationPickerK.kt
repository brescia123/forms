package it.facile.form.model.configuration

import it.facile.form.viewmodel.DescribableK
import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK
import rx.Single

class FieldConfigurationPickerK(label: String,
                                val possibleValuesSingle: Single<List<DescribableK>>,
                                val placeHolder: String) : FieldConfigurationK(label) {
    var possibleValues: List<DescribableK>? = null

    override fun getViewModel(value: FieldValueK, hidden: Boolean): FieldViewModelK {
        return FieldViewModelK(label, getViewModelStyle(value), hidden, null)
    }

    override fun getViewModelStyle(value: FieldValueK): FieldViewModelStyleK =
            when (value) {
                is FieldValueK.Object ->
                    if (possibleValues != null)
                        FieldViewModelStyleK.Picker(
                                possibleValues as List<DescribableK>,
                                value.value?.describe() ?: placeHolder)
                    else FieldViewModelStyleK.Loading()
                else -> FieldViewModelStyleK.InvalidType()
            }

    fun observe(): Single<Unit> {
        return possibleValuesSingle.doOnSuccess {
            possibleValues = it
        }.flatMap { Single.just<Unit>(null) }
    }
}

