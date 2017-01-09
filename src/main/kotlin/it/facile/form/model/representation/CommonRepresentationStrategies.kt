package it.facile.form.model.representation

import it.facile.form.asBool
import it.facile.form.asObject
import it.facile.form.storage.FieldValue
import it.gbresciani.jsonnode.asNode
import it.gbresciani.jsonnode.at

val SIMPLE_KEY_TO_OBJECT_KEY = { key: String, value: FieldValue -> value.asObject()?.value?.key?.asNode() at key }
val SIMPLE_KEY_TO_OBJECT_KEY_AS_INT = { key: String, value: FieldValue -> value.asObject()?.value?.key?.toInt().asNode() at key }
val SIMPLE_KEY_TO_TEXT = { key: String, value: FieldValue -> value.toNode() at key }
val SIMPLE_KEY_TO_BOOL = { key: String, value: FieldValue -> value.asBool()?.bool?.asNode() at key }
val SIMPLE_KEY_TO_BOOL_AS_Y_N = { key: String, value: FieldValue -> value.asBool()?.bool?.toStringValue("Y", "N").asNode() at key }
val SIMPLE_KEY_TO_BOOL_AS_ON_OFF = { key: String, value: FieldValue -> value.asBool()?.bool?.toStringValue("on", "off").asNode() at key }

fun Boolean.toStringValue(trueValue: String, falseValue: String) = if (this == true) trueValue else falseValue
