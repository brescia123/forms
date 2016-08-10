package it.facile.form.viewmodel

data class SectionViewModel(val title: String,
                            val fields: List<FieldViewModel>) : ViewModel {
    override fun isHidden(): Boolean {
        return fields.filter { !it.isHidden() }.size == 0
    }
}
