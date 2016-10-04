package it.facile.form.ui

import it.facile.form.storage.FieldValue
import it.facile.form.ui.viewmodel.FieldPath

data class FieldPathWithValue(val path: FieldPath, val value: FieldValue)

infix fun FieldPath.pathTo(value: FieldValue) = FieldPathWithValue(this, value)