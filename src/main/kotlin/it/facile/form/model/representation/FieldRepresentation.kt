package it.facile.form.model.representation

import it.facile.form.model.representation.FieldRepresentationRule.IF_VISIBLE
import it.facile.form.model.representation.FieldRepresentationRule.NEVER
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorageApi
import it.gbresciani.jsonnode.Node
import it.gbresciani.jsonnode.Node.ObjectNode

fun FieldValue.toNode() = when (this) {
    FieldValue.Missing -> Node.Null
    is FieldValue.Text -> Node.Text(text)
    is FieldValue.Bool -> Node.Bool(bool)
    is FieldValue.DateValue -> Node.Text(date.toString())
    is FieldValue.Object -> Node.ObjectNode.empty().with(value.textDescription, at = value.key)
}

enum class FieldRepresentationRule {
    IF_VISIBLE,
    ALWAYS,
    NEVER
}

interface FieldRepresentationApi {
    fun build(key: String, storage: FormStorageApi): ObjectNode?
}

data class FieldRepresentation(val rule: FieldRepresentationRule, val strategy: ((String, FieldValue) -> ObjectNode)? = null) : FieldRepresentationApi {
    override fun build(key: String, storage: FormStorageApi): ObjectNode? {
        if (rule == NEVER) return null
        if (rule == IF_VISIBLE && storage.isHidden(key)) return null
        return strategy?.invoke(key, storage.getValue(key))
    }
}

infix fun FieldRepresentationRule.representAs(strategy: (String, FieldValue) -> ObjectNode): FieldRepresentation = FieldRepresentation(this, strategy)