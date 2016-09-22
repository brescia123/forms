package it.facile.form.model.models

import it.facile.form.model.FieldConfig
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel

data class FieldModel(val key: Int, val fieldConfiguration: FieldConfig) {
    fun buildFieldViewModel(storage: FormStorage): FieldViewModel {
        return fieldConfiguration.getViewModel(key, storage)
    }
}
