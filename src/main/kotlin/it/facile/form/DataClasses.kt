package it.facile.form

import it.facile.form.viewmodel.FieldPath
import it.facile.form.viewmodel.FieldValue

data class FieldPathWithValue(val path: FieldPath, val value: FieldValue)
data class Entry(val value: FieldValue, val hidden: Boolean = false)
