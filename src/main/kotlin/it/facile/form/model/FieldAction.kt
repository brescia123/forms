package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.viewmodel.FieldValue

class FieldAction(val action: (Int, FieldValue, FormStorage) -> Unit) {
    fun execute(key: Int, value: FieldValue, storage: FormStorage) {
        action(key, value, storage)
    }
}
