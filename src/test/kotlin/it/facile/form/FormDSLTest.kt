package it.facile.form

import io.kotlintest.specs.ShouldSpec
import it.facile.form.model.*
import it.facile.form.model.representation.FieldRepresentationRule.ALWAYS
import it.facile.form.model.representation.representAs
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.keyTo
import it.facile.form.ui.viewmodel.FieldPath

class FormDSLTest : ShouldSpec() {
    private val PAGE1_TITLE = "Page 1 Title"
    private val PAGE2_TITLE = "Page 2 Title"
    private val SECTION1_TITLE = "Section 1 Title"
    private val SECTION2_TITLE = "Section 2 Title"
    private val SECTION3_TITLE = "Section 3 Title"
    private val FIELDKEY1 = "fieldKey1"
    private val FIELDKEY2 = "fieldKey2"
    private val FIELDKEY3 = "fieldKey3"
    private val FIELDKEY4 = "fieldKey4"
    private val FIELDKEY5 = "fieldKey5"
    private val FIELDKEY6 = "fieldKey6"
    private val FIELDKEY7 = "fieldKey7"

    val form = form {
        page(PAGE1_TITLE) {
            section(SECTION1_TITLE) {
                checkbox(FIELDKEY1) {
                    label = "Checkbox Field Label"
                    boolToStringConverter = { if (it == true) "Yes" else "No" }
                    rules = { listOf(NotMissing()) }
                }
                checkbox(FIELDKEY6) {}
                picker(FIELDKEY2) {
                    label = "Picker Field Label"
                    placeHolder = "Select a value"
                    possibleValues = FieldPossibleValues.Available(listOf(
                            1 keyTo "Value1",
                            2 keyTo "Value2",
                            3 keyTo "Value3"))

                }
            }
            section(SECTION2_TITLE) {
                picker(FIELDKEY4) {
                    label = "Picker Field Label"
                    placeHolder = "Select a value"
                    possibleValues = FieldPossibleValues.Available(listOf(
                            1 keyTo "Value1",
                            2 keyTo "Value2",
                            3 keyTo "Value3"))

                }
                input(FIELDKEY3) {
                    label = "Input Text Field Label"
                    inputTextType = InputTextType.EMAIL
                    rules = { listOf(IsEmail()) }

                }
                empty("Empty Field")
            }
            section(SECTION3_TITLE) {
                toggle(FIELDKEY5) {
                    label = "Toggle Field Label"
                    boolToStringConverter = { if (it == true) "OK" else "KO" }
                    rules = { listOf(NotMissing()) }
                }
            }
        }
        page(PAGE2_TITLE) {

        }
    }

    init {
        "DSL" {
            should("add the correct number of pages with correct titles") {
                form.pages.size shouldBe 2
                form.pages[0].title shouldBe PAGE1_TITLE
                form.pages[1].title shouldBe PAGE2_TITLE
            }
            should("add the correct number of sections with correct titles") {
                form.pages[0].sections.size shouldBe 3
                form.pages[1].sections.size shouldBe 0
                form.pages[0].sections[0].title shouldBe SECTION1_TITLE
                form.pages[0].sections[1].title shouldBe SECTION2_TITLE
                form.pages[0].sections[2].title shouldBe SECTION3_TITLE
            }
            should("add a checkbox field with given parameters") {
                form.getField(FieldPath(0, 0, 0)).key shouldBe FIELDKEY1
                form.getField(FieldPath(0, 0, 0)).configuration.label shouldBe "Checkbox Field Label"
            }
            should("add a checkbox field with default parameters") {

            }
        }
    }
}