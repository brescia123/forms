package it.facile.form.model.models

import it.facile.form.model.FieldsContainer
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.SectionViewModel

data class SectionModel(val title: String, val fields: MutableList<FieldModel> = arrayListOf<FieldModel>()) : FieldsContainer {

    override fun fields() = fields.toList()

    fun buildSectionViewModel(storage: FormStorageApi) = SectionViewModel(
            title = title,
            fields = fields.map { it.buildFieldViewModel(storage) })
}

