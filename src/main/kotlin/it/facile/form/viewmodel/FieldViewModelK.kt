package it.facile.form.viewmodel

data class FieldViewModelK(val label: String,
                           val style: FieldViewModelStyleK,
                           val hidden: Boolean,
                           val error: String?) {
}