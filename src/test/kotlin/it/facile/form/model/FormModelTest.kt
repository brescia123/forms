package it.facile.form.model

import io.kotlintest.mock.mock
import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec
import it.facile.form.CustomGen
import it.facile.form.anyKObject
import it.facile.form.model.configurations.FieldConfigBool
import it.facile.form.model.models.FormModel
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorageApi
import it.facile.form.ui.viewmodel.FieldPath
import org.mockito.Matchers.anyBoolean
import org.mockito.Matchers.anyString
import org.mockito.Mockito.*
import rx.observers.TestSubscriber

class FormModelTest : ShouldSpec() {
    init {

        "FormModel.fields" {
            should("build correct FieldModel list") {
                forAll(Gen.string()) { title ->
                    val pages = CustomGen.pageModelList().generate()
                    val expectedFieldModels = pages.flatMap { it.fields() }
                    val pageModel = FormModel(CustomGen.formStorage().generate(), pages.toMutableList(), mutableListOf())
                    pageModel.fields() == expectedFieldModels
                }
            }
        }

        "FormModel.notifyValueChanged" {

            should("do nothing if the FieldPath is not contained within the FormModel") {
                val storage: FormStorageApi = mock()
                val formModel = CustomGen.formModel(storage).generate()
                val path = CustomGen.fieldPath(formModel, contained = false).generate()
                formModel.notifyValueChanged(path, CustomGen.fieldValue().generate())
                verify(storage, never()).putValue(anyString(), anyKObject(), anyBoolean())
            }

            should("put the value into the storage if the FieldPath is contained") {
                val storage: FormStorageApi = mock()
                val formModel = CustomGen.formModel(storage).generate()
                val path = CustomGen.fieldPath(formModel, contained = true).generate()
                formModel.notifyValueChanged(path, CustomGen.fieldValue().generate())
                verify(storage, times(1)).putValue(anyString(), anyKObject(), anyBoolean())
            }
        }

        "FormModel.observeChanges" {
            should("execute actions of the notified key if the change is user-made") {
                forAll(CustomGen.fieldValue()) { value ->
                    val storage: FormStorageApi = CustomGen.formStorage().generate()
                    val model: FormModel = CustomGen.formModel(storage).generate()
                    var actionExecuted = false
                    val key = Gen.string().generate()
                    model.addAction(key to { v, s -> actionExecuted = true })
                    model.observeChanges().subscribe({}, {})
                    val valueAlreadyPresent = value == storage.getValue(key)
                    storage.putValue(key, value, userMade = true)
                    actionExecuted != valueAlreadyPresent
                }
            }

            should("not execute actions of the notified key if the change is not user-made") {
                forAll(CustomGen.fieldValue()) { value ->
                    val storage: FormStorageApi = CustomGen.formStorage().generate()
                    val model: FormModel = CustomGen.formModel(storage).generate()
                    var actionExecuted = false
                    val key = Gen.string().generate()
                    model.addAction(key to { v, s -> actionExecuted = true })
                    model.observeChanges().subscribe({}, {})
                    storage.putValue(key, value, userMade = false)
                    actionExecuted == false
                }
            }

            should("emit also field path for interested keys") {
                val key = "key"
                val observedKey = "observedKey"
                val observedKeyReader = object : WithKey {
                    override val key = observedKey
                }
                val storage: FormStorageApi = CustomGen.formStorage().generate()
                val rule = object : FieldRule {
                    override val errorMessage = "errorMessage"

                    override fun verify(value: FieldValue) = true

                    override fun observedKeys(): List<WithKey> = listOf(observedKeyReader)

                }
                val model: FormModel = FormModel.form(storage, listOf()) {
                    page("page") {
                        section("section") {
                            field(key = key,
                                    config = FieldConfigBool(
                                            label = "label",
                                            viewStyle = FieldConfigBool.ViewStyle.CHECKBOX,
                                            rules = { s -> listOf(rule) }))
                            field(key = observedKey,
                                    config = FieldConfigBool(
                                            label = "label",
                                            viewStyle = FieldConfigBool.ViewStyle.CHECKBOX,
                                            rules = { s -> listOf() }))
                        }
                    }
                }
                val fieldPath = FieldPath(0, 0, 0)
                val observedFieldPath = FieldPath(1, 0, 0)
                val testSubscriber = TestSubscriber<FieldPath>()
                model.observeChanges().subscribe(testSubscriber)
                storage.ping(observedKey)
                testSubscriber.assertNoErrors()
                testSubscriber.assertValues(observedFieldPath, fieldPath)
            }
        }
    }
}
