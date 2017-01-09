package it.facile.form.model

import io.kotlintest.mock.mock
import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec
import it.facile.form.CustomGen
import it.facile.form.Dates
import it.facile.form.model.models.FieldModel
import it.facile.form.model.models.PageModel
import it.facile.form.model.models.SectionModel
import it.facile.form.model.representation.FieldRepresentationApi
import it.facile.form.storage.Entry
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.storage.keyTo
import org.mockito.Mockito

class ModelsTest : ShouldSpec() {

    var storage = FormStorage(emptyMap())
    val valuesTable = table(
            headers("key", "entry"),
            row("key1Bool", Entry(FieldValue.Bool(true), hidden = true)),
            row("key2Text", Entry(FieldValue.Text("textValue"), disabled = true)),
            row("key3Object", Entry(FieldValue.Object("key" keyTo "Description"))),
            row("key4DateValue", Entry(FieldValue.DateValue(Dates.today()), hidden = true)),
            row("key5Missing", Entry(FieldValue.Missing)),
            row("key6Bool", Entry(FieldValue.Bool(false))),
            row("key7Bool", Entry(FieldValue.Bool(false), disabled = true, hidden = true)),
            row("key8Object", Entry(FieldValue.Object("key2" keyTo "Description")))
    )

    override fun beforeEach() {
        valuesTable.rows.forEach {
            storage.putValue(it.a, it.b.value)
            storage.setVisibility(it.a, it.b.hidden)
            if (it.b.disabled) storage.disable(it.a) else storage.enable(it.a)
        }
    }

    init {
        "FieldModel.buildRepresentation" {
            should("call FieldRepresentation.build") {
                forAll(Gen.string(), CustomGen.fieldConfig()) { key, config ->
                    val representationMock: FieldRepresentationApi = mock()
                    val fieldModel = FieldModel(key, representationMock, config)
                    fieldModel.buildRepresentation(storage)
                    Mockito.verify(representationMock).build(key, storage)
                    true
                }
            }
        }

        "FormModel.buildFieldViewModel" {
            should("call FieldConfig.getViewModel") {
                forAll(Gen.string(), CustomGen.fieldRepresentation()) { key, representation ->
                    val configMock: FieldConfigApi = mock()
                    val fieldModel = FieldModel(key, representation, configMock)
                    fieldModel.buildFieldViewModel(storage)
                    Mockito.verify(configMock).getViewModel(key, storage)
                    true
                }
            }
        }

        "SectionModel.buildFieldViewModel" {
            should("build correct SectionViewModel") {
                forAll(Gen.string()) { title ->
                    val fields = CustomGen.fieldModelList().generate()
                    val expectedFieldViewModels = fields.map { it.buildFieldViewModel(storage) }
                    val sectionModel = SectionModel(title, fields.toMutableList())
                    val sectionViewModel = sectionModel.buildSectionViewModel(storage)
                    sectionViewModel.title == title &&
                            sectionViewModel.fields == expectedFieldViewModels
                }
            }
        }

        "PageModel.buildFieldViewModel" {
            should("build correct PageViewModel") {
                forAll(Gen.string()) { title ->
                    val sections = CustomGen.sectionModelList().generate()
                    val expectedSectionsViewModels = sections.map { it.buildSectionViewModel(storage) }
                    val pageModel = PageModel(title, sections.toMutableList())
                    val pageViewModel = pageModel.buildPageViewModel(storage)
                    pageViewModel.title == title && pageViewModel.sections == expectedSectionsViewModels
                }
            }
            should("build correct FieldModel list") {
                forAll(Gen.string()) { title ->
                    val sections = CustomGen.sectionModelList().generate()
                    val expectedFieldModels = sections.flatMap { it.fields }
                    val pageModel = PageModel(title, sections.toMutableList())
                    pageModel.fields() == expectedFieldModels
                }
            }
        }
    }
}