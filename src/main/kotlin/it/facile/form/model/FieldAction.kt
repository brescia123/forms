package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldValue

class FieldAction(val action: (FieldValue, FormStorage) -> Unit) {
    fun execute(value: FieldValue, storage: FormStorage) {
        action( value, storage)
    }
}
