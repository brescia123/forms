package it.facile.form.ui

import it.facile.form.FormStorage
import it.facile.form.buildFieldViewModels
import it.facile.form.model.FormModel

class FormPagePresenter(val pageIndex: Int,
                        val formModel: FormModel,
                        val storage: FormStorage) : Presenter<FormView>() {

    companion object {
        val TAG: String = "FormPagePresenter"
    }

    override fun attach(view: FormView) {
        super.attach(view)
        val sectionViewModels = formModel.pages[pageIndex].buildSectionViewModels(storage)
        val fieldViewModels = buildFieldViewModels(formModel.pages[pageIndex].fields(), storage)
        view.init(sectionViewModels, fieldViewModels)
        view.observeValueChanges().retry().subscribe(
                { formModel.notifyValueChanged(it.first, it.second) },
                { view.logE(TAG, it.message) })
        formModel.observeChanges().subscribe(
                {
                    val (fieldPath, fieldViewModel) = it
                    val absolutePosition = formModel.pages[pageIndex].buildAbsoluteFieldPositionFromFieldPath(fieldPath)
                    if (fieldPath.pageIndex == pageIndex && absolutePosition != null) {
                        val sectionViewModel = formModel.pages[pageIndex].buildSectionViewModel(fieldPath.sectionIndex, storage)
                        view.updateField(absolutePosition, fieldViewModel, sectionViewModel)
                    }
                },
                { view.logE(TAG, it.message) }
        )
    }

    override fun detach() {
        super.detach()
    }
}