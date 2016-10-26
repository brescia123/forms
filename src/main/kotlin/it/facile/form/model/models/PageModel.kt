package it.facile.form.model.models

import it.facile.form.model.FieldsContainer
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.PageViewModel

data class PageModel(val title: String) : FieldsContainer {

    val sections = arrayListOf<SectionModel>()

    override fun fields(): List<FieldModel> {
        return sections.fold(mutableListOf<FieldModel>(),
                { allFields, section ->
                    allFields.addAll(section.fields())
                    allFields
                })
    }

    fun buildPageViewModel(storage: FormStorage): PageViewModel {
        return PageViewModel(
                title,
                sections.map { it.buildSectionViewModel(storage) }
        )
    }

    /** Type-safe builder method to add a section */
    fun section(title: String, init: SectionModel.() -> Unit): SectionModel {
        val section = SectionModel(title)
        section.init()
        sections.add(section)
        return section
    }
}
