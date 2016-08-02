package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle

abstract class FieldConfig(val label: String) :
        ViewModelGenerator,
        ViewModelStyleGenerator {
}

interface ViewModelGenerator {
    fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel
}

interface ViewModelStyleGenerator {
    fun getViewModelStyle(value: FieldValue): FieldViewModelStyle
}