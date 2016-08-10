package it.facile.form.ui

import it.facile.form.FormStorage
import it.facile.form.model.FormModel
import it.facile.form.viewmodel.FieldPath

class FormPresenter(val formModel: FormModel,
                    val storage: FormStorage) : Presenter<FormView>() {

    companion object {
        val TAG: String = "FormPresenter"
    }

    override fun attach(view: FormView) {
        super.attach(view)
        view.init(formModel.pages.map { it.buildPageViewModel(storage) })  // Init view with viewModels

        // Observe value from view (FieldPathValue)
        view.observeValueChanges().retry().subscribe(
                { formModel.notifyValueChanged(it.path, it.value) },
                { view.logE(TAG, it.message) })

        // Observe viewModel from model (FieldPathViewModel)
        formModel.observeChanges().subscribe(
                {
                    val absolutePosition = buildAbsoluteFieldPositionFromFieldPath(it.path, formModel)
                    if (absolutePosition != null) {
                        val sectionViewModel = formModel.pages[it.path.pageIndex].sections[it.path.sectionIndex].buildSectionViewModel(storage)
                        view.updateField(it.path.pageIndex, absolutePosition, it.viewModel, sectionViewModel)
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