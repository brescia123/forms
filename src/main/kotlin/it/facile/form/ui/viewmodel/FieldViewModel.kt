package it.facile.form.ui.viewmodel

import it.facile.form.ui.ViewModel
import it.facile.form.ui.ViewTypeFactory
import it.facile.form.ui.Visitable

data class FieldViewModel(val label: String,
                          val style: FieldViewModelStyle,
                          val hidden: Boolean,
                          val disabled: Boolean,
                          val error: String?) : ViewModel, Visitable {
    override fun isHidden(): Boolean {
        return hidden
    }

    override fun viewType(viewTypeFactory: ViewTypeFactory): Int {
        return style.viewType(viewTypeFactory)
    }
}