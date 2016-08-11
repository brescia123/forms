package it.facile.form.viewmodel

data class PageViewModel(val title: String,
                         val sections: List<SectionViewModel>) : ViewModel {
    override fun isHidden(): Boolean {
        return sections.filter { !it.isHidden() }.size == 0
    }
}