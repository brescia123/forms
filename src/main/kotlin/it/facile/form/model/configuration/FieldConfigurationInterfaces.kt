package it.facile.form.model.configuration

import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK

interface FieldConfigurationK : FieldConfigurationViewModelGenerator, FieldConfigurationViewModelStyleGenerator {
    fun label(): String
}

interface FieldConfigurationViewModelGenerator {
    fun getViewModel(value: FieldValueK, hidden: Boolean): FieldViewModelK
}

interface FieldConfigurationViewModelStyleGenerator {
    fun getViewModelStyle(value: FieldValueK): FieldViewModelStyleK
}