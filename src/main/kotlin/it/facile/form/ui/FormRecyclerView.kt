package it.facile.form.ui

import android.content.Context
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import it.facile.form.R
import it.facile.form.addTo
import it.facile.form.logE
import it.facile.form.logW
import it.facile.form.model.models.FormModel
import it.facile.form.model.models.FormModel.FormState.*
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.ui.adapters.SectionsAdapter
import it.facile.form.ui.utils.FormDefaultItemAnimator
import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.FieldPathSection
import it.facile.form.ui.viewmodel.SectionViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

open class FormRecyclerView(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : this(context, attrs) {
        RecyclerView(context, attrs, defStyle)
    }

    private val subscriptions by lazy { CompositeSubscription() }
    private val formLoadingErrorSnackbar: Snackbar by lazy {
        Snackbar.make(this, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(retryMessage, {
                    // Defer retry to allow the snackbar to fade away
                    Handler().postDelayed({ formModel.loadDynamicValues() }, 400)
                })
    }

    var errorMessage = "There was an error when trying to load the form"
    var retryMessage = "Retry"
    var errorVisualization = true
    var pageIndex = 0
    var formModel: FormModel = FormModel(FormStorage.empty(), mutableListOf(), mutableListOf())
        set(value) {
            field = value
            initModel()
        }

    init {
        itemAnimator = FormDefaultItemAnimator()
        layoutManager = LinearLayoutManager(context)
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FormRecyclerView,
                0, 0)
        try {
            if (a.hasValue(R.styleable.FormRecyclerView_errorMessage)) errorMessage = a.getString(R.styleable.FormRecyclerView_errorMessage)
            if (a.hasValue(R.styleable.FormRecyclerView_retryMessage)) retryMessage = a.getString(R.styleable.FormRecyclerView_retryMessage)
            errorVisualization = a.getBoolean(R.styleable.FormRecyclerView_errorVisualization, true)
        } finally {
            a.recycle()
        }
    }

    override fun setItemAnimator(animator: ItemAnimator?) {
        if (animator !is FormDefaultItemAnimator)
            logW("Using an ItemAnimator that is not a FormItemAnimator can lead to problems " +
                    "with input text fields.")
        super.setItemAnimator(animator)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        (adapter as? SectionsAdapter)?.let { initView(it) }
        logW("Using an Adapter that is not a SectionsAdapter the model will not be notified of value changes.")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        subscriptions.clear()
    }

    fun showFormError(show: Boolean, smoothScrollToFirstError: Boolean = false) {
        (adapter as? SectionsAdapter)?.let {
            it.showErrors(show)
            if (smoothScrollToFirstError) smoothScrollToPosition(it.firstErrorPosition())
        }
    }

    private fun initModel() {

        // Observe paths from model (FieldPath) and update the View
        formModel.observeChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it.pageIndex == pageIndex }
                .map { it to formModel.pages[pageIndex].buildPageViewModel(formModel.storage).sections[it.sectionIndex] }
                .subscribe(
                        { updateViewIfSet(it.first, it.second) }, // TODO: use deconstruction
                        { logE(it.message) })
                .addTo(subscriptions)

        // Observe model loading state if we want to be notified about errors
        if (errorVisualization) {
            formModel.observeFormState()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                when (it) {
                                    NOT_INITIALIZED -> formLoadingErrorSnackbar.dismiss()
                                    READY -> formLoadingErrorSnackbar.dismiss()
                                    LOADING -> formLoadingErrorSnackbar.dismiss()
                                    ERROR -> formLoadingErrorSnackbar.show()
                                    null -> formLoadingErrorSnackbar.show()
                                }
                            },
                            { logE(it) }
                    )
                    .addTo(subscriptions)
        }

        // Start dynamic loading
        formModel.loadDynamicValues()
    }

    private fun initView(sectionsAdapter: SectionsAdapter) {
        // Observe values changes (FieldPathValue) and notify new values to the model
        sectionsAdapter.observeValueChanges()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .retry()
                .subscribe(
                        { notifyModel(it.first, it.second) }, // TODO: use deconstruction
                        { logE(it.message) })
                .addTo(subscriptions)
    }

    private fun notifyModel(fieldPathSection: FieldPathSection, value: FieldValue) {
        formModel.notifyValueChanged(FieldPath(fieldPathSection.fieldIndex, fieldPathSection.sectionIndex, pageIndex), value)
    }

    private fun updateViewIfSet(fieldPath: FieldPath, sectionViewModel: SectionViewModel) {
        (adapter as? SectionsAdapter)?.updateField(fieldPath, sectionViewModel)
    }

}