package it.facile.form.model.models

import it.facile.form.model.FieldConfig
import it.facile.form.model.FieldsContainer
import it.facile.form.model.serialization.FieldSerialization
import it.facile.form.model.serialization.FieldSerializationRule.NEVER
import it.facile.form.model.serialization.FieldSerializationStrategy.None
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.SectionViewModel

data class SectionModel(val title: String, val fields: MutableList<FieldModel> = arrayListOf<FieldModel>()) : FieldsContainer {

    override fun fields() = fields.toList()

    fun buildSectionViewModel(storage: FormStorageApi) = SectionViewModel(
            title = title,
            fields = fields.map { it.buildFieldViewModel(storage) })

    /** Type-safe builder method to add a field */
    fun field(key: String,
              serialization: FieldSerialization = FieldSerialization(NEVER, None),
              config: FieldConfig): FieldModel {
        val field = FieldModel(key, serialization, config)
        fields.add(field)
        return field
    }
}

