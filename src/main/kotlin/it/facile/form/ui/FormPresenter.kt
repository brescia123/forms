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
        view.init(formModel.pages.map { it.buildPageViewModel(storage) })  // Init view with viewModels

        // Observe values from view (FieldPathValue)
        view.observeValueChanges().retry().subscribe(
                { formModel.notifyValueChanged(it.path, it.value) },
                { view.logE(TAG, it.message) })

        // Observe paths from model (FieldPath)
        formModel.observeChanges().subscribe(
                {
                    val fieldViewModel = formModel.getField(it).buildFieldViewModel(storage)
                    val sectionViewModel = formModel.getSection(it).buildSectionViewModel(storage)
                    view.updateField(it, fieldViewModel, sectionViewModel)
                },
                { view.logE(TAG, it.message) }
        )
    }

    override fun detach() {
        super.detach()
    }
}