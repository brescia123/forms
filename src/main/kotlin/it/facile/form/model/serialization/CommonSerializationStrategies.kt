package it.facile.form.model.serialization

import it.facile.form.asBool
import it.facile.form.asObject
import it.facile.form.asText
import it.facile.form.storage.FieldValue


/* ---------- Common Key Serializers ---------- */

val SIMPLE_KEY: (String) -> RemoteKey = { key: String -> RemoteKey(key) }

/* ---------- Common Value Serializers ---------- */

val OBJECT_KEY = { value: FieldValue -> value.asObject()?.value?.key }
val OBJECT_KEY_AS_INT = { value: FieldValue -> value.asObject()?.value?.key?.toInt() }
val INT = { value: FieldValue -> value.asText()?.text?.toInt() }
val TEXT = { value: FieldValue -> value.asText()?.text }
val BOOL = { value: FieldValue -> value.asBool()?.bool }
val BOOL_AS_Y_N = { value: FieldValue -> value.asBool()?.bool?.toStringValue("Y", "N") }
val BOOL_AS_ON_OFF = { value: FieldValue -> value.asBool()?.bool?.toStringValue("on", "off") }

fun Boolean.toStringValue(trueValue: String, falseValue: String) =
        if (this == true) trueValue else falseValue

