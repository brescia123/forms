package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.viewmodel.PageViewModel

data class PageModel internal constructor(val title: String) : FieldsContainer {

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
