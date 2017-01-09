package it.facile.form.model.models

import it.facile.form.model.FieldConfigApi
import it.facile.form.model.representation.FieldRepresentation
import it.facile.form.model.representation.FieldRepresentationApi
import it.facile.form.model.representation.FieldRepresentationRule.NEVER
import it.facile.form.storage.FormStorageApi

data class FieldModel(val key: String,
                      val representation: FieldRepresentationApi = FieldRepresentation(NEVER),
                      val configuration: FieldConfigApi) {
    fun buildFieldViewModel(storage: FormStorageApi) = configuration.getViewModel(key, storage)
    fun buildRepresentation(storage: FormStorageApi) = representation.build(key, storage)
}
