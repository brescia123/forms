package it.facile.form.model

import it.facile.form.model.configuration.FieldConfigurationK

class SectionModelK(val title: String) : FieldsContainer {

    val fields = arrayListOf<FieldModelK>()

    override fun fields(): List<FieldModelK> {
        return fields.toList()
    }

    /** Type-safe builder method to add a field */
    fun field(key: Int, init: () -> FieldConfigurationK): FieldModelK {
        val field = FieldModelK(key, init())
        fields.add(field)
        return field
    }
}

