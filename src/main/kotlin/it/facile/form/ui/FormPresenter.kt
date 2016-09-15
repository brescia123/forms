package it.facile.form.ui

import it.facile.form.FormStorage
import it.facile.form.logE
import it.facile.form.model.FormModel
import rx.subscriptions.CompositeSubscription

class FormPresenter(val formModel: FormModel,
                    val storage: FormStorage) : Presenter<FormView>() {

    private val subscriptions by lazy { CompositeSubscription() }

    override fun attach(view: FormView) {
        super.attach(view)
        view.init(formModel.pages.map { it.buildPageViewModel(storage) })  // Init view with viewModels

        // Observe values from view (FieldPathValue)
        subscriptions.add(
                view.observeValueChanges().retry().subscribe(
                        { formModel.notifyValueChanged(it.path, it.value) },
                        { logE(it.message) }))


        // Observe paths from model (FieldPath)
        subscriptions.add(
                formModel.observeChanges().subscribe(
                        {
                            val fieldViewModel = formModel.getField(it).buildFieldViewModel(storage)
                            val sectionViewModel = formModel.getSection(it).buildSectionViewModel(storage)
                            view.updateField(it, fieldViewModel, sectionViewModel)
                        },
                        { logE(it.message) }))

    }

    override fun detach() {
        super.detach()
        subscriptions.clear()
    }
}