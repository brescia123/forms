package it.facile.form.model.serialization

import it.facile.form.model.serialization.FieldSerializationRule.IF_VISIBLE
import it.facile.form.model.serialization.FieldSerializationRule.NEVER
import it.facile.form.model.serialization.FieldSerializationStrategy.*
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage


class RemoteKey(vararg val path: String) {}

fun RemoteKey.head() = path.first()
fun RemoteKey.tail() = path.filterIndexed { i, _ignored -> i != 0 }
fun RemoteKey.size() = path.size

class FieldSerializer(val keySerializer: (String) -> RemoteKey = { key: String -> RemoteKey(key) },
                      val valueSerializer: (FieldValue) -> Any? = FieldValue::toString) {
    fun serialize(key: String, value: FieldValue) = keySerializer(key) to valueSerializer(value)
}

enum class FieldSerializationRule {
    IF_VISIBLE,
    ALWAYS,
    NEVER
}

sealed class FieldSerializationStrategy() {
    object None : FieldSerializationStrategy()
    class Single(val serializer: FieldSerializer = FieldSerializer()) : FieldSerializationStrategy()
    class Multiple(val serializers: List<FieldSerializer>) : FieldSerializationStrategy()
}

class FieldSerialization(val rule: FieldSerializationRule, val strategy: FieldSerializationStrategy) {
    fun serialize(key: String, storage: FormStorage): List<Pair<RemoteKey, Any?>>? {

        if (rule == NEVER) return null

        RemoteKey()
        if (rule == IF_VISIBLE && storage.isHidden(key)) return null

        val value: FieldValue = storage.getValue(key)

        return when (strategy) {
            None -> null
            is Single -> listOf(strategy.serializer.serialize(key, value))
            is Multiple -> strategy.serializers.map { it.serialize(key, value) }
        }
    }
}

/** Class delegating to a mutable map of String and Any? */
data class NodeMap(val map: MutableMap<String, Any?>) : MutableMap<String, Any?> by map {

    constructor(vararg pairs: Pair<String, Any?>) : this(mutableMapOf(*pairs))

    /** This method return a [NodeMap] build from a path specified inside a [RemoteKey] using as leaf
     * the value contained within the [remoteKeyValue] second element. */
    fun fromRemoteKeyValue(remoteKeyValue: Pair<RemoteKey, Any?>): NodeMap {

        val (key, value) = remoteKeyValue

        if (key.size() == 0) return this

        if (key.size() == 1) {
            put(key.head(), value)
            return this
        }

        val head = key.head()
        val node = get(head)
        val tailRemoteKey = RemoteKey(*key.tail().toTypedArray())
        if (containsKey(head) && node is NodeMap) {
            put(head, node.fromRemoteKeyValue(tailRemoteKey to value))
        } else {
            put(head, NodeMap(mutableMapOf()).fromRemoteKeyValue(tailRemoteKey to value))
        }
        return this
    }

    companion object {
        fun empty() = NodeMap(mutableMapOf())
    }
}