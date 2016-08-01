package it.facile.form.model

import it.facile.form.FormStorageK
import it.facile.form.model.configuration.FieldConfigurationK
import it.facile.form.viewmodel.FieldViewModelK

data class FieldModelK(val key: Int, val fieldConfiguration: FieldConfigurationK) {
    fun buildFieldViewModel(storage: FormStorageK, hidden: Boolean): FieldViewModelK {
        return fieldConfiguration.getViewModel(storage.getValue(key), hidden)
    }
}
