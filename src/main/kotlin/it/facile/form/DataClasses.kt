package it.facile.form

import it.facile.form.viewmodel.FieldPath
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel

data class FieldPathWithViewModel(val path: FieldPath, val viewModel: FieldViewModel)
data class FieldPathWithValue(val path: FieldPath, val value: FieldValue)
data class FieldValueWithVisibility(val value: FieldValue, val hidden: Boolean)
