package it.facile.form.viewmodel

data class FieldViewModel(val label: String,
                          val style: FieldViewModelStyle,
                          val hidden: Boolean,
                          val error: String?) : ViewModel {
    override fun isHidden(): Boolean {
        return hidden
    }
}