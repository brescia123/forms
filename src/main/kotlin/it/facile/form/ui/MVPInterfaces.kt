package it.facile.form.ui

import it.facile.form.FieldPathWithValue
import it.facile.form.viewmodel.FieldPath
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.PageViewModel
import it.facile.form.viewmodel.SectionViewModel
import rx.Observable

abstract class Presenter<T : View> {
    var v: T? = null
    open fun attach(view: T) {
        v = view
    }

    open fun detach() {
        v = null
    }
}

interface View {
}

interface FormView : View {
    fun init(pageViewModels: List<PageViewModel>)
    fun updateField(path: FieldPath,
                    viewModel: FieldViewModel,
                    sectionViewModel: SectionViewModel)
    fun observeValueChanges(): Observable<FieldPathWithValue>
}