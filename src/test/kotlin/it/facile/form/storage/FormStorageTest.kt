package it.facile.form.storage

import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec
import it.facile.form.CustomGen
import it.facile.form.Dates
import it.facile.form.storage.FieldValue.*
import it.facile.form.toSingle
import rx.observers.TestSubscriber

class FormStorageTest : ShouldSpec() {

    lateinit var storage: FormStorageApi
    val nonExistingKeys = arrayOf("nonExistingKey1", "NonExistingKey2", "nonExistingKey3", "NonExistingKey4", "nonExistingKe5", "NonExistingKey6")
    val possibleValuesMap = mapOf(
            "key3Object" to FieldPossibleValues.Available(listOf("key1" keyTo "Desc1", "key2" keyTo "Desc2")),
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
        "FormStorageApi.getValue" {
            should("return value if present") {
                forAll(valuesTable) { key, entry -> storage.getValue(key) shouldBe entry.value }
            }
            should("return Missing if no value is present for the given key") {
                forAll(nonExistingKeys) { storage.getValue(it) shouldBe Missing }
            }
        }

        "FormStorageApi.isHidden" {
            should("return the right hidden status if value is present") {
                forAll(valuesTable) { key, entry -> storage.isHidden(key) shouldBe entry.hidden }
            }
            should("return false if no value is present for the given key") {
                forAll(nonExistingKeys) { storage.isHidden(it) shouldBe false }
            }
        }

        "FormStorageApi.isDisabled" {
            should("return the right disabled status if value is present") {
                forAll(valuesTable) { key, entry -> storage.isDisabled(key) shouldBe entry.disabled }
            }
            should("return false if no value is present for the given key") {
                forAll(nonExistingKeys) { storage.isDisabled(it) shouldBe false }
            }
        }

        "FormStorageApi.getPossibleValues" {
            should("return the right possible values if present") {
                forAll(possibleValuesMap.toList().toTypedArray()) { storage.getPossibleValues(it.first) shouldBe it.second }
            }
            should("return null if no possible values are present for the given key") {
                forAll(nonExistingKeys) { storage.getPossibleValues(it) shouldBe null }
            }
        }

        "FormStorageApi.ping" {
            should("trigger an event for the given key") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(valuesTable) { key, entry -> storage.ping(key) }
                testSubscriber.assertNoErrors()
                testSubscriber.assertValueCount(valuesTable.rows.size)
                val expectedValues = valuesTable.rows.map { it.a to false }
                testSubscriber.assertValues(*expectedValues.toTypedArray())
            }
        }

        "FormStorageApi.putValue" {
            should("put value at given key") {
                forAll(Gen.string(), CustomGen.fieldValue()) { key, value ->
                    storage.putValue(key, value)
                    storage.getValue(key) == value
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(Gen.string(), CustomGen.fieldValue(), Gen.bool()) { key, value, userMade ->
                    if (storage.getValue(key) != value) expectedEvents.add(key to userMade)
                    storage.putValue(key, value, userMade)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "FormStorageApi.enable" {
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

        "FormStorageApi.disable" {
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

        "FormStorageApi.clearValue" {
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
                val expectedValues = valuesTable.rows.map { it.a to false }
                testSubscriber.assertValues(*expectedValues.toTypedArray())
            }
            should("notify the change if user-made") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(valuesTable) { key, entry ->
                    storage.clearValue(key, true)
                }
                testSubscriber.assertNoErrors()
                val expectedValues = valuesTable.rows.map { it.a to true }
                testSubscriber.assertValues(*expectedValues.toTypedArray())
            }
        }

        "FormStorageApi.setVisibility" {
            should("set correct visibility to value at given key") {
                forAll(Gen.string(), Gen.bool()) { key, visibility ->
                    storage.setVisibility(key, visibility)
                    storage.isHidden(key) == visibility
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(Gen.string(), Gen.bool()) { key, visibility ->
                    if (storage.isHidden(key) != visibility) expectedEvents.add(key to false)
                    storage.setVisibility(key, visibility)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "FormStorageApi.putPossibleValues" {
            should("put possible values at given key") {
                forAll(possibleValuesMap.entries.toTypedArray()) {
                    val possibleValues = CustomGen.possibleValues().generate()
                    storage.putPossibleValues(it.key, possibleValues)
                    storage.getPossibleValues(it.key) shouldBe possibleValues
                }
                forAll(Gen.string(), CustomGen.possibleValues()) { key, possibleValues ->
                    storage.putPossibleValues(key, possibleValues)
                    storage.getPossibleValues(key) == possibleValues
                }
            }
            should("clear value at given key") {
                forAll(possibleValuesMap.entries.toTypedArray()) {
                    val possibleValues = CustomGen.possibleValues().generate()
                    storage.putPossibleValues(it.key, possibleValues)
                    if (storage.getPossibleValues(it.key) != possibleValues)
                        storage.getValue(it.key) shouldBe Missing
                }
                forAll(Gen.string(), CustomGen.possibleValues()) { key, possibleValues ->
                    storage.putPossibleValues(key, possibleValues)
                    if (storage.getPossibleValues(key) != possibleValues)
                        storage.getValue(key) shouldBe Missing
                    true
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(possibleValuesMap.entries.toTypedArray()) {
                    val possibleValues = CustomGen.possibleValues().generate()
                    if (storage.getPossibleValues(it.key) != possibleValues) expectedEvents.add(it.key to false)
                    storage.putPossibleValues(it.key, possibleValues)
                }
                forAll(Gen.string(), CustomGen.possibleValues()) { key, possibleValues ->
                    if (storage.getPossibleValues(key) != possibleValues) expectedEvents.add(key to false)
                    storage.putPossibleValues(key, possibleValues)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "FormStorageApi.switchPossibleValues" {
            should("switch possible values at given key") {
                forAll(possibleValuesMap.entries.toTypedArray()) {
                    val possibleValues = CustomGen.possibleValues().generate()
                    storage.switchPossibleValues(it.key, possibleValues)
                    storage.getPossibleValues(it.key) shouldBe possibleValues
                }
                forAll(Gen.string(), CustomGen.possibleValues()) { key, possibleValues ->
                    storage.switchPossibleValues(key, possibleValues)
                    storage.getPossibleValues(key) == possibleValues
                }
            }
            should("leave selected value if new and old PossibleValues are compatible") {
                storage.putPossibleValues("key3Object", FieldPossibleValues.Available(listOf("key1" keyTo "Desc1", "key2" keyTo "Desc2")))
                storage.putValue("key3Object", Object("key1" keyTo "Desc1"))
                storage.switchPossibleValues("key3Object", FieldPossibleValues.Available(listOf("key1" keyTo "DESC1", "key2" keyTo "DESC2")))
                storage.getValue("key3Object") shouldBe Object("key1" keyTo "DESC1")
            }
            should("leave clear selected value if new and old PossibleValues are incompatible") {
                storage.putPossibleValues("key3Object", FieldPossibleValues.Available(listOf("key1" keyTo "Desc1", "key2" keyTo "Desc2")))
                storage.putValue("key3Object", Object("key1" keyTo "Desc1"))
                storage.switchPossibleValues("key3Object", FieldPossibleValues.Available(listOf("Key1" keyTo "DESC1", "key2" keyTo "DESC2")))
                storage.getValue("key3Object") shouldBe Missing
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(possibleValuesMap.entries.toTypedArray()) {
                    val possibleValues = CustomGen.possibleValues().generate()
                    if (storage.getPossibleValues(it.key) != possibleValues) expectedEvents.add(it.key to false)
                    storage.switchPossibleValues(it.key, possibleValues)
                }
                forAll(Gen.string(), CustomGen.possibleValues()) { key, possibleValues ->
                    if (storage.getPossibleValues(key) != possibleValues) expectedEvents.add(key to false)
                    storage.switchPossibleValues(key, possibleValues)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "FormStorageApi.putValueAndSetVisibility" {
            should("set correct visibility an put value at given key") {
                forAll(Gen.string(), CustomGen.fieldValue(), Gen.bool()) { key, value, visibility ->
                    storage.putValueAndSetVisibility(key, value, visibility)
                    storage.isHidden(key) == visibility && storage.getValue(key) == value
                }
            }
            should("notify the change") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                val expectedEvents = mutableListOf<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                forAll(Gen.string(), CustomGen.fieldValue(), Gen.bool()) { key, value, visibility ->
                    if (storage.isHidden(key) != visibility || storage.getValue(key) != value)
                        expectedEvents.add(key to false)
                    storage.putValueAndSetVisibility(key, value, visibility)
                    true
                }
                testSubscriber.assertValues(*expectedEvents.toTypedArray())
                testSubscriber.assertNoErrors()
            }
        }

        "FormStorageApi.clearPossibleValues" {
            should("clear possible values at given key") {
                storage.putPossibleValues("key3Object", FieldPossibleValues.Available(listOf("key1" keyTo "Desc1", "key2" keyTo "Desc2")))
                storage.clearPossibleValues("key3Object")
                storage.getPossibleValues("key3Object") shouldBe null
            }
            should("not notify the change if values were not present") {
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                storage.clearPossibleValues("missingPossibleValuesKey")
                testSubscriber.assertNoErrors()
                testSubscriber.assertNoValues()
            }
            should("notify the change if values were present") {
                storage.putPossibleValues("key3Object", FieldPossibleValues.Available(listOf("key1" keyTo "Desc1", "key2" keyTo "Desc2")))
                val testSubscriber = TestSubscriber<Pair<String, Boolean>>()
                storage.observe().subscribe(testSubscriber)
                storage.clearPossibleValues("key3Object")
                testSubscriber.assertNoErrors()
                testSubscriber.assertValue("key3Object" to false)
            }

        }
    }
}