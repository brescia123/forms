package it.facile.form.viewmodel

import it.facile.form.model.FormModel

data class FieldPathIndex(val fieldIndex: Int)
data class FieldPathSection(val fieldIndex: Int, val sectionIndex: Int)
data class FieldPath(val fieldIndex: Int, val sectionIndex: Int, val pageIndex: Int) {
    companion object {

        fun buildForKey(key: Int, formModel: FormModel): List<FieldPath> {
            var pageIndex = 0
            var fieldPaths = mutableListOf<FieldPath>()
            for (page in formModel.pages) {
                var sectionIndex = 0
                for (section in page.sections) {
                    var fieldIndex = 0
                    for (field in section.fields) {
                        if (field.key == key) fieldPaths.add(FieldPath(fieldIndex, sectionIndex, pageIndex))
                        fieldIndex++
                    }
                    sectionIndex++
                }
                pageIndex++
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
