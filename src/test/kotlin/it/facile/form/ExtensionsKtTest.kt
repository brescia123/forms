package it.facile.form

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun testEqualMap_whenEquals() {
        // When...
        val map1: MutableMap<Int, String> = mutableMapOf(0 to "w", 2 to "s")
        val map2: MutableMap<Int, String> = mutableMapOf(0 to "w", 2 to "s")

        // Assert...
        assertTrue(map1.equalMap(map2))
        assertTrue(map2.equalMap(map2))
    }

    @Test
    fun testEqualMap_whenDifferent() {
        // When...
        val map1: MutableMap<Int, String> = mutableMapOf(0 to "w")
        val map2: MutableMap<Int, String> = mutableMapOf(0 to "e")
        val map3: MutableMap<Int, String> = mutableMapOf(1 to "e")
        val map4: MutableMap<Int, String> = mutableMapOf(1 to "w")
        val map5: MutableMap<Int, String> = mutableMapOf(0 to "w", 2 to "s")
        val map6: MutableMap<Int, String> = mutableMapOf(0 to "w", 2 to "3")
        val map7: MutableMap<Int, String> = mutableMapOf(2 to "s", 0 to "w")


        // Assert...
        assertFalse(map1.equalMap(map2))
        assertFalse(map1.equalMap(map2))
        assertFalse(map1.equalMap(map3))
        assertFalse(map1.equalMap(map4))
        assertFalse(map1.equalMap(map5))
        assertFalse(map1.equalMap(map6))
        assertFalse(map1.equalMap(map7))
        assertFalse(map2.equalMap(map3))
        assertFalse(map2.equalMap(map4))
        assertFalse(map2.equalMap(map5))
        assertFalse(map2.equalMap(map6))
        assertFalse(map2.equalMap(map7))
        assertFalse(map3.equalMap(map4))
        assertFalse(map3.equalMap(map5))
        assertFalse(map3.equalMap(map6))
        assertFalse(map3.equalMap(map7))
        assertFalse(map4.equalMap(map5))
        assertFalse(map4.equalMap(map6))
        assertFalse(map4.equalMap(map7))
        assertFalse(map5.equalMap(map6))
        assertTrue(map5.equalMap(map7))
        assertFalse(map6.equalMap(map7))
    }
}