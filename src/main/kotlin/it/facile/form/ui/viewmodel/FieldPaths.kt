package it.facile.form.ui.viewmodel

import it.facile.form.model.models.FormModel

data class FieldPathIndex(val fieldIndex: Int)
data class FieldPathSection(val fieldIndex: Int, val sectionIndex: Int)
data class FieldPath(val fieldIndex: Int, val sectionIndex: Int, val pageIndex: Int) {
    companion object {

        fun buildForKey(key: String, formModel: FormModel): List<FieldPath> {
            val fieldPaths = mutableListOf<FieldPath>()
            for ((pageIndex, page) in formModel.pages.withIndex()) {
                for ((sectionIndex, section) in page.sections.withIndex()) {
                    for ((fieldIndex, field) in section.fields.withIndex()) {
                        if (field.key == key) fieldPaths.add(FieldPath(fieldIndex, sectionIndex, pageIndex))
                    }
                }
            }
            return fieldPaths
        }
    }

    override fun toString(): String {
        return "FieldPath(" + pageIndex +
                "," + sectionIndex +
                "," + fieldIndex +
                ')'
    }
}
