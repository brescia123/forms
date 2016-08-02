package it.facile.form.model

import it.facile.form.model.configuration.FieldConfig

class SectionModel(val title: String) : FieldsContainer {

    val fields = arrayListOf<FieldModel>()

    override fun fields(): List<FieldModel> {
        return fields.toList()
    }

    /** Type-safe builder method to add a field */
    fun field(key: Int, init: () -> FieldConfig): FieldModel {
        val field = FieldModel(key, init())
        fields.add(field)
        return field
    }
}

