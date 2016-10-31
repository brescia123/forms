package it.facile.form.model.configurations

import it.facile.form.model.FieldConfig
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.Action

class FieldConfigCustomBehaviour(label: String,
                                 val customBehaviourId: String) : FieldConfig(label) {

    override fun getViewModel(key: String, storage: FormStorage) = FieldViewModel(
            label = label,
            style = getViewModelStyle(key, storage),
            hidden = storage.isHidden(key),
            disabled = storage.isDisabled(key),
            error = null
    )

    override fun getViewModelStyle(key: String, storage: FormStorage) = Action(key, customBehaviourId)
}