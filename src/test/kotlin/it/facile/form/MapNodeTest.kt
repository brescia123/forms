package it.facile.form

import io.kotlintest.specs.StringSpec
import it.facile.form.model.serialization.NodeMap
import it.facile.form.model.serialization.RemoteKey
import org.mockito.Matchers.any

class MapNodeTest : StringSpec() {
    init {
        "NodeMap.fromRemoteKeyValue" should {
            "return correct NodeMap with empty RemoteKey" {
                NodeMap.empty().fromRemoteKeyValue(RemoteKey() to any()) shouldBe NodeMap(mutableMapOf())
            }
            "return correct NodeMap with single path RemoteKey" {
                val mapNode = NodeMap.empty().fromRemoteKeyValue(RemoteKey("path1") to any())
                mapNode shouldBe NodeMap(mutableMapOf("path1" to any()))
            }
            "return correct NodeMap with multiple path RemoteKey" {
                val mapNode = NodeMap.empty().fromRemoteKeyValue(RemoteKey("path1", "path2", "path3") to any())
                mapNode shouldBe NodeMap("path1" to NodeMap("path2" to NodeMap("path3" to any())))
            }
            "return correct NodeMap with path step empty" {
                val mapNode = NodeMap.empty().fromRemoteKeyValue(RemoteKey("path1", "", "path3") to any())
                mapNode shouldBe NodeMap("path1" to NodeMap("" to NodeMap("path3" to any())))
            }
        }
    }
}