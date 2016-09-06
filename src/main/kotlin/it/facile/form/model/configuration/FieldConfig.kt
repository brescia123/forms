package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import rx.Single

abstract class FieldConfig(val label: String) : ViewModelGenerator, ViewModelStyleGenerator {}

interface ViewModelGenerator {
    fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel
}

interface ViewModelStyleGenerator {
    fun getViewModelStyle(value: FieldValue): FieldViewModelStyle
}

interface DeferredConfig {
    fun observe(): Single<Unit>
}