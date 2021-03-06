package it.facile.form.model.models

import it.facile.form.model.FieldsContainer
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.PageViewModel

data class PageModel(val title: String, val sections: MutableList<SectionModel> = arrayListOf<SectionModel>()) : FieldsContainer {

    override fun fields(): List<FieldModel> = sections.flatMap { it.fields }

    fun buildPageViewModel(storage: FormStorageApi) = PageViewModel(
            title,
            sections.map { it.buildSectionViewModel(storage) }
    )
}
