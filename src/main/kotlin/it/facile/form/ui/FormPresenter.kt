package it.facile.form.ui

import it.facile.form.FormStorage
import it.facile.form.model.FormModel

class FormPresenter(val formModel: FormModel,
                    val storage: FormStorage) : Presenter<FormView>() {

    companion object {
        val TAG: String = "FormPresenter"
    }

    override fun attach(view: FormView) {
        super.attach(view)
        view.init(formModel.pages.map { it.buildPageViewModel(storage) })  // I
        view.observeValueChanges().retry().subscribe(
                { formModel.notifyValueChanged(it.first, it.second) },
                { view.logE(TAG, it.message) })
        formModel.observeChanges().subscribe(
                {
                    val (fieldPath, fieldViewModel) = it
                    val absolutePosition = formModel.pages[fieldPath.pageIndex].buildAbsoluteFieldPositionFromFieldPath(fieldPath)
                    if (absolutePosition != null) {
                        val sectionViewModel = formModel.pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].buildSectionViewModel(storage)
                        view.updateField(fieldPath.pageIndex, absolutePosition, fieldViewModel, sectionViewModel)
                    }
                },
                { view.logE(TAG, it.message) }
        )
    }

    override fun detach() {
        super.detach()
    }

    private fun buildAbsoluteFieldPositionFromFieldPath(fieldPath: FieldPath, formModel: FormModel): Int? {
        var absolutePosition = 0
        val sections = formModel.pages[fieldPath.pageIndex].sections
        for (i in 0..sections.size - 1) {
            for (j in 0..sections[i].fields.size - 1) {
                if (i == fieldPath.sectionIndex && j == fieldPath.fieldIndex) return absolutePosition
                absolutePosition++
            }
        }
        return null
    }
}