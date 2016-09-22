package it.facile.form.model

import it.facile.form.storage.FormStorage
import it.facile.form.storage.FieldValue

class FieldAction(val action: (FieldValue, FormStorage) -> Unit) {
    fun execute(value: FieldValue, storage: FormStorage) {
        action( value, storage)
    }
}
