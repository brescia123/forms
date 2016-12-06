package it.facile.form.model.configurations

import it.facile.form.model.FieldConfig
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle

class FieldConfigEmpty(label: String) : FieldConfig(label) {
    override fun getViewModel(key: String, storage: FormStorageApi) =
            FieldViewModel(
                    label = label,
                    style = getViewModelStyle(key, storage),
                    hidden = storage.isHidden(key),
                    disabled = storage.isDisabled(key),
                    error = null)

    override fun getViewModelStyle(key: String, storage: FormStorageApi) =
            FieldViewModelStyle.Empty
}