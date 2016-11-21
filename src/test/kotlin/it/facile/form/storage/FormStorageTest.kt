package it.facile.form.storage

import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec
import it.facile.form.Dates
import it.facile.form.FieldValueGen
import it.facile.form.storage.FieldValue.*
import it.facile.form.toSingle
import rx.observers.TestSubscriber

class FormStorageTest : ShouldSpec() {

    lateinit var storage: FormStorage
    val nonExistingKeys = arrayOf("nonExistingKey1", "NonExistingKey2", "nonExistingKey3", "NonExistingKey4", "nonExistingKe5", "NonExistingKey6")
    val possibleValuesMap = mapOf(
            "key3Object" to FieldPossibleValues.Available(listOf("e" keyTo "Desc")),
            "key8Object" to FieldPossibleValues.ToBeRetrieved(listOf("e" keyTo "Desc").toSingle())
    )
    val valuesTable = table(
            headers("key", "entry"),
            row("key1Bool", Entry(Bool(true), hidden = true)),
            row("key2Text", Entry(Text("textValue"), disabled = true)),
            row("key3Object", Entry(Object("key" keyTo "Description"))),
            row("key4DateValue", Entry(DateValue(Dates.today()), hidden = true)),
            row("key5Missing", Entry(Missing)),
            row("key6Bool", Entry(Bool(false))),
            row("key7Bool", Entry(Bool(false), disabled = true, hidden = true)),
            row("key8Object", Entry(Object("key2" keyTo "Description")))
    )

    override fun beforeEach() {
        storage = FormStorage(emptyMap())
        possibleValuesMap.forEach { storage.putPossibleValues(it.key, it.value) }
        valuesTable.rows.forEach {
            storage.putValue(it.a, it.b.value)
            storage.setVisibility(it.a, it.b.hidden)
            if (it.b.disabled) storage.disable(it.a) else storage.enable(it.a)
        }
    }

    init {
        "FormStorage.getValue" {
            should("return value if present") {
                forAll(valuesTable) { key, entry -> storage.getValue(key) shouldBe entry.value }
            }
            should("return Missing if no value is present for the given key") {
                forAll(nonExistingKeys) { storage.getValue(it) shouldBe Missing }
            }
        }

        "FormStorage.isHidden" {
            should("return the right hidden status if value is present") {
                forAll(valuesTable) { key, entry -> storage.isHidden(key) shouldBe entry.hidden }
            }
            should("return false if no value is present for the given key") {
                forAll(nonExistingKeys) { storage.isHidden(it) shouldBe false }
            }
        }

        "FormStorage.isDisabled" {
            should("return the right disabled status if value is present") {
                forAll(valuesTable) { key, entry -> storage.isDisabled(key) shouldBe entry.disabled }
            }
            should("return false if no value is present for the given key") {
                forAll(nonExistingKeys) { storage.isDisabled(it) shouldBe false }
            }
        }

        "FormStorage.getPossibleValues" {
            should("return the right possible values if present") {
                forAll(possibleValuesMap.toList().toTypedArray()) { storage.getPossibleValues(it.first) shouldBe it.second }
            }
            should("return null if no possible values are present for the given key") {
                forAll(nonExistingKeys) { storage.getPossibleValues(it) shouldBe null }
            }
        }

        "FormStorage.ping" {
            should("trigger an event for the given key") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(valuesTable) { key, entry -> storage.ping(key) }
                testSubscriber.assertNoErrors()
                testSubscriber.assertValueCount(valuesTable.rows.size)
                testSubscriber.assertValues(*valuesTable.rows.map { it.a to false }.toTypedArray())
            }
        }

        "Form.putValue" {
            should("put value at given key") {
                forAll(Gen.string(), FieldValueGen) { key, value ->
                    storage.putValue(key, value)
                    storage.getValue(key) == value
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(Gen.string(), FieldValueGen, Gen.bool()) { key, value, userMade ->
                    if (storage.getValue(key) != value) expectedEvents.add(key to userMade)
                    storage.putValue(key, value, userMade)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "Form.enable" {
            should("enable value at given key") {
                forAll(valuesTable) { key, entry ->
                    storage.enable(key)
                    storage.getValue(key) shouldBe entry.value
                    storage.isDisabled(key) == false
                }
                forAll(Gen.string()) { key ->
                    storage.enable(key)
                    storage.getValue(key) shouldBe Missing
                    storage.isDisabled(key) == false
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(Gen.string(), Gen.bool()) { key, userMade ->
                    expectedEvents.add(key to userMade)
                    storage.enable(key, userMade)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "Form.disable" {
            should("disable value at given key") {
                forAll(valuesTable) { key, entry ->
                    storage.disable(key)
                    storage.getValue(key) shouldBe entry.value
                    storage.isDisabled(key) == true
                }
                forAll(Gen.string()) { key ->
                    storage.disable(key)
                    storage.getValue(key) shouldBe Missing
                    storage.isDisabled(key) == true
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(Gen.string(), Gen.bool()) { key, userMade ->
                    expectedEvents.add(key to userMade)
                    storage.disable(key, userMade)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }


        "Form.clearValue" {
            should("clear value at given key") {
                forAll(valuesTable) { key, entry ->
                    storage.clearValue(key)
                    storage.getValue(key) shouldBe Missing
                }
                forAll(Gen.string()) { key ->
                    storage.clearValue(key)
                    storage.getValue(key) == Missing
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(valuesTable) { key, entry ->
                    storage.clearValue(key)
                }
                testSubscriber.assertNoErrors()
                testSubscriber.assertValues(*valuesTable.rows.map { it.a to false }.toTypedArray())
            }
            should("notify the change if user-made") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(valuesTable) { key, entry ->
                    storage.clearValue(key, true)
                }
                testSubscriber.assertNoErrors()
                testSubscriber.assertValues(*valuesTable.rows.map { it.a to true }.toTypedArray())
            }
        }

    }
}