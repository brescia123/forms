package it.facile.form.model

import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec
import it.facile.form.*
import it.facile.form.model.serialization.FieldSerialization
import it.facile.form.model.serialization.FieldSerializationRule.*
import it.facile.form.model.serialization.FieldSerializationStrategy.*
import it.facile.form.model.serialization.FieldSerializer
import it.facile.form.model.serialization.NodeMap
import it.facile.form.model.serialization.RemoteKey
import it.facile.form.storage.FieldValue

class SerializationTest : ShouldSpec() {
    init {
        "FieldSerializer.apply" {
            should("call KeySerializer and ValueSerializer with right params") {
                forAll(Gen.string(), FieldValueGen) { key, value ->
                    val keySerializer: (String) -> RemoteKey = { RemoteKey(it) }
                    val valueSerializer: (FieldValue) -> Any? = { value.toString() }
                    val fieldSerializer = FieldSerializer(keySerializer, valueSerializer)
                    val pair = fieldSerializer.serialize(key, value)
                    pair == RemoteKey(key) to value.toString()
                }
            }
        }

        "FieldSerialization.apply" {
            should("return null if rule is NEVER") {
                forAll(FieldSerializationStrategyGen, Gen.string(), FormStorageGen) { strategy, key, storage ->
                    val serialization = FieldSerialization(NEVER, strategy)
                    serialization.apply(key, storage) == null
                }
            }
            should("return null if rule is IF_VISIBLE and the field is hidden") {
                forAll(FieldSerializationStrategyGen, Gen.string(), FormStorageGen) { strategy, key, storage ->
                    val serialization = FieldSerialization(IF_VISIBLE, strategy)
                    storage.setVisibility(key, hidden = true)
                    serialization.apply(key, storage) == null
                }
            }
            should("return null if strategy is None") {
                forAll(FieldSerializationRuleGen, Gen.string(), FormStorageGen) { rule, key, storage ->
                    val serialization = FieldSerialization(rule, None)
                    serialization.apply(key, storage) == null
                }
            }
            should("apply the correct serialization with default serializer and SingleKey") {
                forAll(Gen.string(), FormStorageGen) { key, storage ->
                    val defaultFieldSerializer = FieldSerializer()
                    val serialization = FieldSerialization(ALWAYS, SingleKey(defaultFieldSerializer))
                    serialization.apply(key, storage) == listOf(RemoteKey(key) to storage.getValue(key).toString())
                }
            }
            should("apply the correct serialization with default serializer and MultipleKey") {
                forAll(Gen.string(), FormStorageGen) { key, storage ->
                    val keySerializer = { key: String -> RemoteKey("-$key-") }
                    val valueSerializer = { value: FieldValue -> "-$value-" }
                    val customFieldSerializer = FieldSerializer(keySerializer, valueSerializer)
                    val defaultFieldSerializer = FieldSerializer()
                    val serialization = FieldSerialization(ALWAYS, MultipleKey(listOf(defaultFieldSerializer, customFieldSerializer)))
                    serialization.apply(key, storage) == listOf(
                            RemoteKey(key) to storage.getValue(key).toString(),
                            RemoteKey("-$key-") to "-${storage.getValue(key)}-")
                }
            }
        }

        "NodeMap.empty" {
            should("return an empty NodeMap") {
                NodeMap.empty() shouldBe NodeMap(mutableMapOf())
            }
        }

        "NodeMap.fromRemoteKeyValue" {
            should("return a NodeMap with only one element") {
                forAll(RemoteKeyGen, FieldValueGen) { remoteKey, value ->
                    val nodeMap = NodeMap.empty().fromRemoteKeyValue(remoteKey to value)
                    nodeMap.size == 1
                }
            }
            should("return a NodeMap with the right leaf and depth") {
                forAll(RemoteKeyGen, FieldValueGen) { remoteKey, value ->
                    val nodeMap = NodeMap.empty().fromRemoteKeyValue(remoteKey to value)
                    val leaf = (0..remoteKey.path.size - 2)
                            .fold(nodeMap) { innerNodeMap, i -> innerNodeMap[remoteKey.path[i]] as NodeMap }
                    leaf[remoteKey.path.last()] == value
                }
            }
        }
    }
}