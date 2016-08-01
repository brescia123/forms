package it.facile.form.model

import it.facile.form.viewmodel.FieldPathK
import it.facile.form.viewmodel.SectionViewModelK

class PageModelK(val title: String, vararg val sections: SectionModelK) {

    fun fields(): List<FieldModelK> {
        return sections.fold(mutableListOf<FieldModelK>(),
                { allFields, section ->
                    allFields.addAll(section.fields)
                    allFields
                })
    }

    fun getFieldModelByAbsolutePosition(absolutePosition: Int): FieldModelK = fields()[absolutePosition]

    fun buildSectionViewModels(): List<SectionViewModelK> {
        return sections.foldIndexed(Pair(0, mutableListOf<SectionViewModelK>()),
                { index, offsetSectionPair, section ->
                    offsetSectionPair.second.add(SectionViewModelK(
                            firstPosition = offsetSectionPair.first,
                            sectionedPosition = offsetSectionPair.first + index,
                            title = section.title))
                    val newOffset = offsetSectionPair.first + section.fields.size
                    Pair(newOffset, offsetSectionPair.second)
                }).second
    }

    fun buildAbsoluteFieldPositionFromFieldPath(fieldPath: FieldPathK): Int? {
        var absolutePosition = 0
        for (i in 0..sections.size - 1) {
            for (j in 0..sections[i].fields.size - 1) {
                if (i == fieldPath.sectionIndex && j == fieldPath.fieldIndex) return absolutePosition
                absolutePosition++
            }
        }
        return null
    }
}
