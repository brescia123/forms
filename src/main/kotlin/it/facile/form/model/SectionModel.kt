package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.model.configuration.FieldConfig
import it.facile.form.viewmodel.SectionViewModel

class SectionModel internal constructor (val title: String) : FieldsContainer {

    val fields = arrayListOf<FieldModel>()

    override fun fields(): List<FieldModel> {
        return fields.toList()
    }

    fun buildSectionViewModel(storage: FormStorage): SectionViewModel {
        return SectionViewModel(
                title = title,
                fields = fields.map { it.buildFieldViewModel(storage) }
        )
    }

    /** Type-safe builder method to add a field */
    fun field(key: Int, init: () -> FieldConfig): FieldModel {
        val field = FieldModel(key, init())
        fields.add(field)
        return field
    }
}

