package it.facile.form.ui

import it.facile.form.addTo
import it.facile.form.logE
import it.facile.form.model.models.FormModel
import it.facile.form.model.models.FormModel.FormState
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

abstract class FormPresenter<V : FormView>() : Presenter<V>() {

    protected val formModel: FormModel by lazy { formModelGenerator() }
    protected val subscriptions by lazy { CompositeSubscription() }
    protected var stateErrorShown: Boolean = false

    /** This method should return the [FormModel] to be handled by the presenter */
    abstract fun formModelGenerator(): FormModel

    fun retryLoading() {
        formModel.loadDynamicValues()
    }

    override fun onAttach(v: V) {
        super.onAttach(v)

        // Initialize the View with the page ViewModels
        v.init(formModel.pages.map { it.buildPageViewModel(formModel.storage) })  // Init view with viewModels
        v.showErrors(stateErrorShown) // Restore state

        // Observe values from view (FieldPathValue) and notify new values to the model
        v.observeValueChanges()
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
                        { v.updateField(it, formModel.getPage(it).buildPageViewModel(formModel.storage)) },
                        { logE(it.message) })
                .addTo(subscriptions)

        // Observe model loading state
        formModel.observeFormState()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            when (it!!) {
                                FormState.READY -> this.view?.showLoadingErrors(false)
                                FormState.LOADING -> this.view?.showLoadingErrors(false)
                                FormState.ERROR -> this.view?.showLoadingErrors(true)
                            }
                        },
                        { logE(it) }
                )
                .addTo(subscriptions)

        // Start dynamic loading
        formModel.loadDynamicValues()
    }

    override fun onDetach() {
        super.onDetach()
        subscriptions.clear()
    }

    protected fun showFormErrors(show: Boolean) {
        stateErrorShown = show
        view?.showErrors(show)
        if (show) view?.scrollToFirstError()
    }
}