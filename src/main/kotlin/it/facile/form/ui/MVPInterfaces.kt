package it.facile.form.ui

import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.PageViewModel
import it.facile.form.ui.viewmodel.SectionViewModel

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
}