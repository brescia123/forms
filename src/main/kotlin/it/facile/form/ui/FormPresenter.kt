package it.facile.form.ui

import it.facile.form.addTo
import it.facile.form.logE
import it.facile.form.model.models.FormModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import kotlin.properties.Delegates.observable

abstract class FormPresenter<V : FormView>() : Presenter<V>(), StorageProvider {

    protected val formModel: FormModel by lazy { formModelGenerator() }
    protected val subscriptions by lazy { CompositeSubscription() }
    protected var stateErrorShown: Boolean by observable(false) {
        p, o, new -> if (new) v?.showErrors(stateErrorShown)  // When changed trigger view's method to show errors
    }

    /** This method should return the [FormModel] to be handled by the presenter */
    abstract fun formModelGenerator(): FormModel

    override fun getStorage() = formModel.storage

    override fun onAttach(view: V) {
        super.onAttach(view)

        // Initialize the View with the page ViewModels
        view.init(formModel.pages.map { it.buildPageViewModel(formModel.storage) })  // Init view with viewModels
        view.showErrors(stateErrorShown) // Restore state

        // Observe values from view (FieldPathValue) and notify new values to the model
        view.observeValueChanges()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .retry()
                .subscribe(
                        { formModel.notifyValueChanged(it.path, it.value) },
                        { logE(it.message) })
                .addTo(subscriptions)


        // Observe paths from model (FieldPath) and update the View
        formModel.observeChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { view.updateField(it, formModel.getPage(it).buildPageViewModel(formModel.storage)) },
                        { logE(it.message) })
                .addTo(subscriptions)

    }

    override fun onDetach() {
        super.onDetach()
        subscriptions.clear()
    }
}