package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldPath
import it.facile.form.viewmodel.SectionViewModel

class PageModel(val title: String) : FieldsContainer {

    val sections = arrayListOf<SectionModel>()

    override fun fields(): List<FieldModel> {
        return sections.fold(mutableListOf<FieldModel>(),
                { allFields, section ->
                    allFields.addAll(section.fields())
                    allFields
                })
    }

    fun getFieldModelByAbsolutePosition(absolutePosition: Int): FieldModel = fields()[absolutePosition]

    fun buildSectionViewModels(storage: FormStorage): List<SectionViewModel> {
        return sections.foldIndexed(Pair(0, mutableListOf<SectionViewModel>()),
                { index, offsetSectionPair, section ->
                    offsetSectionPair.second.add(buildSectionViewModel(index, storage))
                    val newOffset = offsetSectionPair.first + section.fields.size
                    Pair(newOffset, offsetSectionPair.second)
                }).second
    }

    fun buildSectionViewModel(index: Int, storage: FormStorage): SectionViewModel {
        val offset = sections.subList(0, index).fold(0,
                { offset, section ->
                    offset + section.fields.size
                })
        return SectionViewModel(
                firstPosition = offset,
                sectionedPosition = offset + index,
                title = sections[index].title,
                hidden = sections[index].fields.filter { !storage.isHidden(it.key) }.size == 0)
    }

    fun buildAbsoluteFieldPositionFromFieldPath(fieldPath: FieldPath): Int? {
        var absolutePosition = 0
        for (i in 0..sections.size - 1) {
            for (j in 0..sections[i].fields.size - 1) {
                if (i == fieldPath.sectionIndex && j == fieldPath.fieldIndex) return absolutePosition
                absolutePosition++
            }
        }
        return null
    }

    /** Type-safe builder method to add a section */
    fun section(title: String, init: SectionModel.() -> Unit): SectionModel {
        val section = SectionModel(title)
        section.init()
        sections.add(section)
        return section
    }
}
