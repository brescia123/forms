package it.facile.form

import it.facile.form.storage.FieldValue

data class Entry(val value: FieldValue, val hidden: Boolean = false)
