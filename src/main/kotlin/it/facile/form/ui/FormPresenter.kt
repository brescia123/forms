package it.facile.form.ui

import it.facile.form.logE
import it.facile.form.model.models.FormModel
import rx.Observable
import rx.subscriptions.CompositeSubscription

class FormPresenter(val formModel: FormModel) : Presenter<FormView>() {

    private val subscriptions by lazy { CompositeSubscription() }

    override fun attach(view: FormView) {
        super.attach(view)
        view.init(formModel.pages.map { it.buildPageViewModel(formModel.storage) })  // Init view with viewModels

        // Observe values from view (FieldPathValue) and notify new values to the model
        subscriptions.add(
                view.observeValueChanges().retry().subscribe(
                        { formModel.notifyValueChanged(it.path, it.value) },
                        { logE(it.message) }))


        // Observe paths from model (FieldPath) and update the View
        subscriptions.add(
                formModel.observeChanges().subscribe(
                        {
                            val pageViewModel = formModel.getPage(it).buildPageViewModel(formModel.storage)
                            view.updateField(it, pageViewModel)
                        },
                        { logE(it.message) }))

    }

    override fun detach() {
        super.detach()
        subscriptions.clear()
    }

    fun registerValueChangesObservable(observable: Observable<FieldPathWithValue>) {
        // Observe values from view (FieldPathValue)
        subscriptions.add(
                observable.retry().subscribe(
                        { formModel.notifyValueChanged(it.path, it.value) },
                        { logE(it.message) }))
    }
}