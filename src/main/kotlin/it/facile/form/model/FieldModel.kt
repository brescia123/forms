package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.model.configuration.FieldConfig
import it.facile.form.viewmodel.FieldViewModel

data class FieldModel(val key: Int, val fieldConfiguration: FieldConfig) {
    fun buildFieldViewModel(storage: FormStorage): FieldViewModel {
        return fieldConfiguration.getViewModel(storage.getValue(key), storage.isHidden(key))
    }
}
