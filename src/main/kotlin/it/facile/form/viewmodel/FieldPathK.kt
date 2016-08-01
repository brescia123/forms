package it.facile.form.viewmodel

import it.facile.form.model.FormModelK

class FieldPathK(val fieldIndex: Int, val sectionIndex: Int, val pageIndex: Int) {
    object Builder {
        fun buildForKey(key: Int, formModel: FormModelK): FieldPathK? {
            var pageIndex = 0
            for (page in formModel.pages) {
                var sectionIndex = 0
                for (section in page.sections) {
                    var fieldIndex = 0
                    for (field in section.fields) {
                        if (field.key == key) return FieldPathK(fieldIndex, sectionIndex, pageIndex)
                        fieldIndex++
                    }
                    sectionIndex++
                }
                pageIndex++
            }
            return null
        }
    }

    override fun toString(): String {
        return "FieldPath(" + pageIndex +
                "," + sectionIndex +
                "," + fieldIndex +
                ')'
    }
}