package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK

abstract class FieldConfigurationK(val label: String) :
        FieldConfigurationViewModelGenerator,
        FieldConfigurationViewModelStyleGenerator {
}

interface FieldConfigurationViewModelGenerator {
    fun getViewModel(value: FieldValueK, hidden: Boolean): FieldViewModelK
}

interface FieldConfigurationViewModelStyleGenerator {
    fun getViewModelStyle(value: FieldValueK): FieldViewModelStyleK
}