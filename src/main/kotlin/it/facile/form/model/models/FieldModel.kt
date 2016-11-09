package it.facile.form.model.models

import it.facile.form.model.FieldConfig
import it.facile.form.model.serialization.FieldSerialization
import it.facile.form.model.serialization.FieldSerializationRule.NEVER
import it.facile.form.model.serialization.FieldSerializationStrategy.None
import it.facile.form.storage.FormStorage

data class FieldModel(val key: String,
                      val serialization: FieldSerialization = FieldSerialization(NEVER, None),
                      val configuration: FieldConfig) {
    fun buildFieldViewModel(storage: FormStorage) = configuration.getViewModel(key, storage)
    fun serialize(storage: FormStorage) = serialization.serialize(key, storage)
}
