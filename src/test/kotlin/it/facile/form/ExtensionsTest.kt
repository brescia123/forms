package it.facile.form

import io.kotlintest.specs.ShouldSpec
import org.junit.Assert
import rx.observers.TestSubscriber
import rx.subscriptions.CompositeSubscription
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class ExtensionsTest : ShouldSpec() {

    init {
        "Map.equalMap" {
            should("return true when maps are equals") {
                forAll(CustomGen.map()) { map: Map<Any, Any> ->
                    map.equalMap(map)
                }
            }
            should("return false when maps are different") {
                val map1: MutableMap<Int, String> = mutableMapOf(0 to "w")
                val map2: MutableMap<Int, String> = mutableMapOf(0 to "e")
                val map3: MutableMap<Int, String> = mutableMapOf(1 to "e")
                val map4: MutableMap<Int, String> = mutableMapOf(1 to "w")
                val map5: MutableMap<Int, String> = mutableMapOf(0 to "w", 2 to "s")
                val map6: MutableMap<Int, String> = mutableMapOf(0 to "w", 2 to "3")
                val map7: MutableMap<Int, String> = mutableMapOf(2 to "s", 0 to "w")

                Assert.assertFalse(map1.equalMap(map2))
                Assert.assertFalse(map1.equalMap(map2))
                Assert.assertFalse(map1.equalMap(map3))
                Assert.assertFalse(map1.equalMap(map4))
                Assert.assertFalse(map1.equalMap(map5))
                Assert.assertFalse(map1.equalMap(map6))
                Assert.assertFalse(map1.equalMap(map7))
                Assert.assertFalse(map2.equalMap(map3))
                Assert.assertFalse(map2.equalMap(map4))
                Assert.assertFalse(map2.equalMap(map5))
                Assert.assertFalse(map2.equalMap(map6))
                Assert.assertFalse(map2.equalMap(map7))
                Assert.assertFalse(map3.equalMap(map4))
                Assert.assertFalse(map3.equalMap(map5))
                Assert.assertFalse(map3.equalMap(map6))
                Assert.assertFalse(map3.equalMap(map7))
                Assert.assertFalse(map4.equalMap(map5))
                Assert.assertFalse(map4.equalMap(map6))
                Assert.assertFalse(map4.equalMap(map7))
                Assert.assertFalse(map5.equalMap(map6))
                Assert.assertTrue(map5.equalMap(map7))
                Assert.assertFalse(map6.equalMap(map7))
            }
        }

        "Any.toSingle" {
            should("return a single emitting just it") {
                val any = Any()
                val testSubscriber = TestSubscriber<Any>()
                any.toSingle().subscribe(testSubscriber)
                testSubscriber.assertValue(any)
            }
        }

        "Subscription.addTo" {
            should("add it to the given CompositeSubscription") {
                val subscriptions =  CompositeSubscription()
                val subscription = Any().toSingle().delay(2, TimeUnit.SECONDS).subscribe({}, {})
                subscription.addTo(subscriptions)
                subscriptions.hasSubscriptions() shouldBe true
                subscriptions.unsubscribe()
                subscription.isUnsubscribed shouldBe true
            }
        }

        "Dates.create" {
            should("create should create right date") {
                val date = Dates.create(2016, 0, 1)
                date.year() shouldBe 2016
                date.month() shouldBe 0
                date.dayOfMonth() shouldBe 1
                val format = SimpleDateFormat()
                format.format(date) shouldEqual date.format(format)
            }
        }

        "String.containsLink" {
            should("return true if string contains link") {
                "http://www.google.it".containsLink() shouldBe true
                "ciaohttp://www.google.it".containsLink() shouldBe true
                "ciao http://www.google.it/path".containsLink() shouldBe true
                "ciao https://www.google.it".containsLink() shouldBe true
            }
            should("return false if string does not contain link") {
                "www.".containsLink() shouldBe false
                "".containsLink() shouldBe false
                "www.google".containsLink() shouldBe false
            }
        }
    }
}


