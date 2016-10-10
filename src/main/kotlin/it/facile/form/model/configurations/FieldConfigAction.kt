package it.facile.form.model.configurations

import it.facile.form.model.FieldConfig
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.Action

class FieldConfigAction(label: String,
                        val action: () -> Unit) : FieldConfig(label) {

    override fun getViewModel(key: String, storage: FormStorage) = FieldViewModel(
            label,
            getViewModelStyle(key, storage),
            storage.isHidden(key),
            null
    )

    override fun getViewModelStyle(key: String, storage: FormStorage) = Action(key, action)
}