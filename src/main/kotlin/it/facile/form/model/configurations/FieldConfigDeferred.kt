package it.facile.form.model.configurations

import it.facile.form.model.CouldHaveLoadingError
import it.facile.form.model.FieldConfig
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.ExceptionText
import it.facile.form.ui.viewmodel.FieldViewModelStyle.Loading
import rx.Single

class FieldConfigDeferred(label: String = "Loading...",
                          val deferredConfig: Single<FieldConfig>,
                          override val errorMessage: String = "Loading error") : FieldConfig(label), CouldHaveLoadingError {
    override var hasErrors = false

    override fun getViewModel(key: String, storage: FormStorage) = FieldViewModel(
            label = label,
            style = getViewModelStyle(key, storage),
            hidden = false,
            disabled = false,
            error = null)

    override fun getViewModelStyle(key: String, storage: FormStorage) = if (hasErrors)
        ExceptionText(errorMessage)
    else
        Loading()
}