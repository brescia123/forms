package it.facile.form.model.serialization

import it.facile.form.model.serialization.FieldSerializationRule.IF_VISIBLE
import it.facile.form.model.serialization.FieldSerializationRule.NEVER
import it.facile.form.model.serialization.FieldSerializationStrategy.*
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import java.util.*

val NEVER_SERIALIZE = NEVER serialize None

class RemoteKey(vararg val path: String) {
    override fun equals(other: Any?): Boolean {
        if (other !is RemoteKey) return false
        return Arrays.equals(other.path, path)
    }

    override fun hashCode(): Int = Arrays.hashCode(path)
}

fun RemoteKey.head() = path.first()
fun RemoteKey.tail() = path.drop(1)
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
    class SingleKey(val serializer: FieldSerializer = FieldSerializer()) : FieldSerializationStrategy()
    class MultipleKey(val serializers: List<FieldSerializer>) : FieldSerializationStrategy()
}

interface FieldSerializationApi {
    fun apply(key: String, storage: FormStorage): List<Pair<RemoteKey, Any?>>?
}

class FieldSerialization(val rule: FieldSerializationRule, val strategy: FieldSerializationStrategy) : FieldSerializationApi {
    override fun apply(key: String, storage: FormStorage): List<Pair<RemoteKey, Any?>>? {
        if (rule == NEVER) return null
        if (rule == IF_VISIBLE && storage.isHidden(key)) return null

        val value: FieldValue = storage.getValue(key)
        return when (strategy) {
            None -> null
            is SingleKey -> listOf(strategy.serializer.serialize(key, value))
            is MultipleKey -> strategy.serializers.map { it.serialize(key, value) }
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

    /** Merge this NodeMap with the given one and return a new NodeMap  */
    fun with(nodeMap: NodeMap?): NodeMap {
        val temp = mutableMapOf(*map.toList().toTypedArray())
        temp.putAll(nodeMap?.map ?: emptyMap())
        return NodeMap(temp)
    }

    /** Merge this NodeMap with the given pair and return a new NodeMap  */
    fun with(pair: Pair<String, Any?>): NodeMap {
        val temp = mutableMapOf(*map.toList().toTypedArray())
        temp.put(pair.first, pair.second)
        return NodeMap(temp)
    }

    companion object {
        fun empty() = NodeMap(mutableMapOf())
    }
}

infix fun FieldSerializationRule.serialize(s: FieldSerializationStrategy) = FieldSerialization(this, s)
infix fun FieldSerializationRule.serialize(s: FieldSerializer) = FieldSerialization(this, SingleKey(s))
infix fun FieldSerializationRule.serialize(s: List<FieldSerializer>) = FieldSerialization(this, MultipleKey(s))
infix fun ((String) -> RemoteKey).to(v: (FieldValue) -> Any?) = FieldSerializer(this, v)