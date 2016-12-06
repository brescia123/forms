package it.facile.form.model.models

import it.facile.form.model.FieldConfigApi
import it.facile.form.model.serialization.FieldSerialization
import it.facile.form.model.serialization.FieldSerializationApi
import it.facile.form.model.serialization.FieldSerializationRule.NEVER
import it.facile.form.model.serialization.FieldSerializationStrategy.None
import it.facile.form.storage.FormStorage

data class FieldModel(val key: String,
                      val serialization: FieldSerializationApi = FieldSerialization(NEVER, None),
                      val configuration: FieldConfigApi) {
    fun buildFieldViewModel(storage: FormStorage) = configuration.getViewModel(key, storage)
    fun serialize(storage: FormStorage) = serialization.apply(key, storage)
}
