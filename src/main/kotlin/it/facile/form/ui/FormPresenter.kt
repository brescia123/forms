package it.facile.form.ui

import it.facile.form.addTo
import it.facile.form.logE
import it.facile.form.model.models.FormModel
import rx.subscriptions.CompositeSubscription

class FormPresenter(val formModel: FormModel) : Presenter<FormView>() {

    private val subscriptions by lazy { CompositeSubscription() }

    override fun attach(view: FormView) {
        super.attach(view)

        // Initialize the View with the page ViewModels
        view.init(formModel.pages.map { it.buildPageViewModel(formModel.storage) })  // Init view with viewModels

        // Observe values from view (FieldPathValue) and notify new values to the model
        view.observeValueChanges()
                .retry()
                .subscribe(
                        { formModel.notifyValueChanged(it.path, it.value) },
                        { logE(it.message) })
                .addTo(subscriptions)


        // Observe paths from model (FieldPath) and update the View
        formModel.observeChanges()
                .subscribe(
                        { view.updateField(it, formModel.getPage(it).buildPageViewModel(formModel.storage)) },
                        { logE(it.message) })
                .addTo(subscriptions)

    }

    override fun detach() {
        super.detach()
        subscriptions.clear()
    }
}