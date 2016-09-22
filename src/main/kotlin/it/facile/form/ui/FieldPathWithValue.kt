package it.facile.form.ui

import it.facile.form.storage.FieldValue
import it.facile.form.ui.viewmodel.FieldPath

data class FieldPathWithValue(val path: FieldPath, val value: FieldValue)