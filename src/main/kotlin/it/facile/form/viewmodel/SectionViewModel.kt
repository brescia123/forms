package it.facile.form.viewmodel

data class SectionViewModel(val firstPosition: Int,
                            val sectionedPosition: Int,
                            val title: String,
                            val hidden: Boolean) : ViewModel {
    override fun isHidden(): Boolean {
        return hidden
    }
}
