package it.facile.form

import it.facile.form.storage.FieldValue

class InvalidFieldValueException(fieldValue: FieldValue, override val message: String?) : Exception("Invalid FieldValue: $fieldValue")