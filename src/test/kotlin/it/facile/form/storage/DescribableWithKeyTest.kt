package it.facile.form.storage

import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec

class DescribableWithKeyTest : ShouldSpec() {
    init {
        "Int.keyTo" {
            should("create correct DescribableWithKey") {
                forAll(Gen.int(), Gen.string()) { key, string ->
                    val describableWithKey = key keyTo string
                    val expected: DescribableWithKey = object : DescribableWithKey {
                        override val key = key.toString()
                        override val textDescription = string
                    }
                    describableWithKey.key == expected.key &&
                            describableWithKey.textDescription == expected.textDescription
                }
            }
        }

        "String.keyTo" {
            should("create correct DescribableWithKey") {
                forAll(Gen.string(), Gen.string()) { key, string ->
                    val describableWithKey = key keyTo string
                    val expected: DescribableWithKey = object : DescribableWithKey {
                        override val key = key
                        override val textDescription = string
                    }
                    describableWithKey.key == expected.key &&
                            describableWithKey.textDescription == expected.textDescription
                }
            }
        }

        "DescribableWithKey components" {
            should("enable deconstruction") {
                forAll(Gen.string(), Gen.string()) { key, string ->
                    val describableWithKey = key keyTo string
                    val (expectedKey, expectedText) = describableWithKey
                    expectedKey == key && expectedText == string
                }
            }
        }
    }
}