package it.facile.form.model.serialization

import it.facile.form.model.serialization.FieldConversionRule.IF_VISIBLE
import it.facile.form.model.serialization.FieldConversionRule.NEVER
import it.facile.form.model.serialization.FieldConversionStrategy.*
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorageApi
import it.gbresciani.jsonnode.Node
import it.gbresciani.jsonnode.Node.ObjectNode
import it.gbresciani.jsonnode.NodePath

val NEVER_CONVERT = NEVER convertWith None


fun FieldValue.toNode() = when (this) {
    FieldValue.Missing -> Node.Null
    is FieldValue.Text -> Node.Text(text)
    is FieldValue.Bool -> Node.Bool(bool)
    is FieldValue.DateValue -> Node.Text(date.toString())
    is FieldValue.Object -> Node.ObjectNode.empty().with(value.textDescription, at = value.key)
}

class FieldToObjectNodeConverter(val keyToNodePath: (String) -> NodePath = { key -> NodePath(key) },
                                 val valueToNode: (FieldValue) -> Node = FieldValue::toNode) {
    fun convert(key: String, value: FieldValue) = ObjectNode().with(valueToNode(value), at = keyToNodePath(key))
}

class FieldSerializer(val keyToNodePath: (String) -> NodePath = { key -> NodePath(key) },
                      val valueToNode: (FieldValue) -> Node = FieldValue::toNode) {
    fun serialize(key: String, value: FieldValue) = keyToNodePath(key) to valueToNode(value)
}

enum class FieldConversionRule {
    IF_VISIBLE,
    ALWAYS,
    NEVER
}

sealed class FieldConversionStrategy() {
    object None : FieldConversionStrategy()
    class SingleKey(val converter: FieldToObjectNodeConverter = FieldToObjectNodeConverter()) : FieldConversionStrategy()
    class MultipleKey(val converters: List<FieldToObjectNodeConverter>) : FieldConversionStrategy()
}

interface FieldConversionApi {
    fun apply(key: String, storage: FormStorageApi): ObjectNode?
}

interface FieldSerializationApi {
    fun apply(key: String, storage: FormStorageApi): List<Pair<NodePath, Node>>?
}


data class FieldConversion(val rule: FieldConversionRule, val strategy: FieldConversionStrategy) : FieldConversionApi {
    override fun apply(key: String, storage: FormStorageApi): ObjectNode? {
        if (rule == NEVER) return null
        if (rule == IF_VISIBLE && storage.isHidden(key)) return null

        val value: FieldValue = storage.getValue(key)
        return when (strategy) {
            None -> null
            is SingleKey -> strategy.converter.convert(key, value)
            is MultipleKey -> strategy.converters.fold(ObjectNode()) { acc, converter ->
                acc.with(converter.valueToNode(value), at = converter.keyToNodePath(key))
            }
        }
    }
}


infix fun FieldConversionRule.convertWith(s: FieldConversionStrategy) = FieldConversion(this, s)

infix fun FieldConversionRule.serializeAs(s: FieldToObjectNodeConverter) = FieldConversion(this, SingleKey(s))
infix fun FieldConversionRule.serializeAs(s: List<FieldToObjectNodeConverter>) = FieldConversion(this, MultipleKey(s))
infix fun ((String) -> NodePath).to(v: (FieldValue) -> Node) = FieldToObjectNodeConverter(this, v)