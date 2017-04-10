package it.facile.form.ui

import it.facile.form.*
import it.facile.form.model.*
import it.facile.form.model.representation.FieldRepresentationRule
import it.facile.form.model.representation.SIMPLE_KEY_TO_OBJECT_KEY
import it.facile.form.model.representation.representAs
import it.facile.form.model.representation.toNode
import it.facile.form.storage.*
import it.gbresciani.jsonnode.Node
import it.gbresciani.jsonnode.Node.ObjectNode
import it.gbresciani.jsonnode.NodePath
import it.gbresciani.jsonnode.asNode
import it.gbresciani.jsonnode.at
import rx.Scheduler

/* ---------- MESSAGES ---------- */
private const val MISSING_TEXT = "Per cortesia seleziona il campo"
private const val INVALID_FIELD = "Il campo deve contenere una stringa valida"
private const val ALMOST_ONE_TEXT = "Selezionare almeno uno dei campi"

class TestForm {

    companion object {

        /* ---------- FIELDS KEYS ---------- */
        const val PICKER_GROUPS = "PICKER_GROUPS"
        // Fields - Group 1
        const val INPUT_TEXT_1_G1 = "input_text_1_g1"
        const val INPUT_TEXT_2_G1 = "input_text_2_g1"
        const val PICKER_1 = "picker_1"
        const val TOGGLE_1 = "toggle_1"
        const val PICKER_2 = "picker_2"
        const val PICKER_3 = "picker_3"
        const val CHECKBOX_1_G1 = "checkbox_1_g1"
        const val CHECKBOX_2_G1 = "checkbox_2_g1"
        const val CHECKBOX_3_G1 = "checkbox_3_g1"
        const val PICKER_4 = "picker_4"
        const val CHECKBOX_5_G1 = "checkbox_5_g1"
        const val CHECKBOX_6_G1 = "checkbox_6_g1"

        // Fields - Group 2
        const val INPUT_TEXT_1_G2 = "input_text_1_g2"
        // Common Fields
        const val INPUT_TEXT_1_COMMON = "input_text_1_common"
        const val INPUT_TEXT_2_COMMON = "input_text_2_common"
        const val CHECKBOX_1_COMMON = "CHECKBOX_1_COMMON"
        const val CHECKBOX_2_COMMON = "CHECKBOX_2_COMMON"
        const val CHECKBOX_3_COMMON = "CHECKBOX_3_COMMON"
        const val CHECKBOX_4_COMMON = "CHECKBOX_4_COMMON"

        const val CUSTOM_PICKER_ID_COMUNE_RESIDENZA = "comune_residenza_custom_picker_id"


        /* ---------- FIELDS ACTIONS ---------- */
        val TOGGLE_GROUPS_ACTIONS = { value: FieldValue, storage: FormStorageApi ->
            storage.setVisibility(TestForm.PICKER_1, hidden = value.asBoolOrFalse(), executeActions = true)
        }
        val ATTUALE_FORNITORE_ACTIONS = { value: FieldValue, storage: FormStorageApi ->
            if (storage.getValue(TestForm.TOGGLE_1).asBoolOrFalse())
                storage.setVisibility(TestForm.PICKER_1, hidden = true)
            else
                storage.setVisibility(TestForm.PICKER_1, hidden = false)
        }


        val FORM_ACTIONS = listOf(
                TestForm.PICKER_GROUPS to { value: FieldValue, storage: FormStorageApi ->
                    when (value.asObject()?.value?.key) {
                        UTENZA_POSSIBLE_VALUES[0].key -> {
                            // Show "GROUP 1" fields
                            storage.setVisibility(TestForm.INPUT_TEXT_1_G1, hidden = false)
                            storage.setVisibility(TestForm.INPUT_TEXT_2_G1, hidden = false)
                            storage.setVisibility(TestForm.PICKER_1, hidden = false)
                            storage.setVisibility(TestForm.TOGGLE_1, hidden = false)
                            storage.setVisibility(TestForm.PICKER_3, hidden = false)
                            storage.setVisibility(TestForm.PICKER_2, hidden = false)
                            storage.setVisibility(TestForm.PICKER_4, hidden = false)
                            storage.setVisibility(TestForm.CHECKBOX_1_G1, hidden = false)
                            storage.setVisibility(TestForm.CHECKBOX_2_G1, hidden = false)
                            storage.setVisibility(TestForm.CHECKBOX_3_G1, hidden = false)
                            storage.setVisibility(TestForm.CHECKBOX_5_G1, hidden = false)
                            storage.setVisibility(TestForm.CHECKBOX_6_G1, hidden = false)
                            storage.setVisibility(TestForm.INPUT_TEXT_1_G2, true)
                            TOGGLE_GROUPS_ACTIONS.invoke(storage.getValue(TestForm.TOGGLE_1), storage)
                        }
                        UTENZA_POSSIBLE_VALUES[1].key -> {
                            // Hide "GROUP 2" fields
                            storage.setVisibility(TestForm.INPUT_TEXT_1_G1, hidden = true)
                            storage.setVisibility(TestForm.INPUT_TEXT_2_G1, hidden = true)
                            storage.setVisibility(TestForm.PICKER_1, hidden = true)
                            storage.setVisibility(TestForm.TOGGLE_1, hidden = true)
                            storage.setVisibility(TestForm.PICKER_3, hidden = true)
                            storage.setVisibility(TestForm.PICKER_2, hidden = true)
                            storage.setVisibility(TestForm.PICKER_4, hidden = true)
                            storage.setVisibility(TestForm.CHECKBOX_1_G1, hidden = true)
                            storage.setVisibility(TestForm.CHECKBOX_2_G1, hidden = true)
                            storage.setVisibility(TestForm.CHECKBOX_3_G1, hidden = true)
                            storage.setVisibility(TestForm.CHECKBOX_5_G1, hidden = true)
                            storage.setVisibility(TestForm.CHECKBOX_6_G1, hidden = true)
                            storage.setVisibility(TestForm.INPUT_TEXT_1_G2, hidden = false)
                        }
                        else -> Unit
                    }
                },
                TestForm.TOGGLE_1 to TOGGLE_GROUPS_ACTIONS,
                TestForm.PICKER_1 to ATTUALE_FORNITORE_ACTIONS
        )

        /* ---------- POSSIBLE VALUES ---------- */
        val UTENZA_POSSIBLE_VALUES = listOf("a" keyTo "Group 1", "b" keyTo "Group 2")

        val POSSIBLE_VALUES_1 = listOf(
                1 keyTo "1",
                2 keyTo "2",
                3 keyTo "3",
                4 keyTo "4",
                5 keyTo "5")

        val POSSIBLE_VALUES_2 = listOf(
                1 keyTo "40",
                2 keyTo "60",
                3 keyTo "80",
                4 keyTo "100",
                5 keyTo "100")

        val POSSIBLE_VALUES_3 = listOf(
                1 keyTo "string_1",
                2 keyTo "string_2",
                4 keyTo "string_3",
                0 keyTo "string_4")

        val FORM_MODEL = { storage: FormStorage, workScheduler: Scheduler, resultScheduler: Scheduler ->
            form(storage, FORM_ACTIONS, workScheduler = workScheduler, resultScheduler = resultScheduler) {
                page("Form") {
                    section("Section1 (Position 0)") {
                        picker(TestForm.PICKER_GROUPS) {
                            label = "FieldPicker (Position 1)"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(UTENZA_POSSIBLE_VALUES)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.ALWAYS representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        toggle(TestForm.TOGGLE_1) {
                            label = "FieldToggle (Position 2)"
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                        }
                        picker(TestForm.PICKER_1)
                        {
                            label = "FieldPicker (Position 3)"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(POSSIBLE_VALUES_3)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs { key, value -> value.asObjectKey().let { if (it != "0") ObjectNode().with(it, at = NodePath(key, "attualeFornitore")) else null } ?: Node.ObjectNode.empty() }
                        }
                        picker(TestForm.PICKER_2) {
                            label = "FieldPicker (Position 4)"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(POSSIBLE_VALUES_1)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        picker(TestForm.PICKER_4) {
                            label = "FieldPicker (Position 5)"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(POSSIBLE_VALUES_2)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                    }
                    section("Section2 (Position 6)") {
                        checkbox(TestForm.CHECKBOX_1_G1) {
                            label = "Checkbox (Position 7)"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.CHECKBOX_1_G1, TestForm.CHECKBOX_2_G1, TestForm.CHECKBOX_3_G1)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                        checkbox(TestForm.CHECKBOX_2_G1) {
                            label = "Checkbox (Position 8)"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.CHECKBOX_2_G1, TestForm.CHECKBOX_1_G1, TestForm.CHECKBOX_3_G1)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                        checkbox(TestForm.CHECKBOX_3_G1) {
                            label = "Checkbox (Position 9)"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.CHECKBOX_3_G1, TestForm.CHECKBOX_2_G1, TestForm.CHECKBOX_1_G1)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                    }
                    section("Section2 (Position 10)") {
                        checkbox(TestForm.CHECKBOX_5_G1) {
                            label = "Checkbox (Position 11)"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.CHECKBOX_5_G1, TestForm.CHECKBOX_6_G1)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                        checkbox(TestForm.CHECKBOX_6_G1) {
                            label = "Checkbox (Position 12)"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.CHECKBOX_6_G1, TestForm.CHECKBOX_5_G1)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                    }
                    section("Section3 (Position 13)") {
                        input(TestForm.INPUT_TEXT_1_G1) {
                            label = "Input (Position 14)"
                            inputTextConfig = InputTextConfig(InputTextType.CapWords)
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsName(INVALID_FIELD)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                        input(TestForm.INPUT_TEXT_2_G1) {
                            label = "Input (Position 15)"
                            inputTextConfig = InputTextConfig(InputTextType.CapWords)
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsName(INVALID_FIELD)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                        pickerCustom(TestForm.PICKER_3, TestForm.CUSTOM_PICKER_ID_COMUNE_RESIDENZA) {
                            label = "FieldPicker (Position 16)"
                            placeHolder = ""
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }

                        input(TestForm.INPUT_TEXT_1_G2) {
                            label = "Input (Position 17)"
                            inputTextConfig = InputTextConfig(InputTextType.CapWords)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }

                        input(TestForm.INPUT_TEXT_1_COMMON) {
                            label = "Input (Position 18)"
                            inputTextConfig = InputTextConfig(InputTextType.Phone)
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsCellularPhone(INVALID_FIELD)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                        input(TestForm.INPUT_TEXT_2_COMMON) {
                            label = "Input (Position 19)"
                            inputTextConfig = InputTextConfig(InputTextType.Email)
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsEmail(INVALID_FIELD)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                    }
                    section("Section4 (Position 20)") {
                        checkbox(CHECKBOX_1_COMMON) {
                            label = "Checkbox (Position 21)"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        checkbox(CHECKBOX_2_COMMON) {
                            label = "Checkbox (Position 22)"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        checkbox(CHECKBOX_3_COMMON) {
                            label = "Checkbox (Position 23)"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        checkbox(CHECKBOX_4_COMMON) {
                            label = "Checkbox (Position 24)"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                    }
                }
            }
        }

    }

    class AlmostOneSelected(override val errorMessage: String = "Almost one of the fields must be selected", val storage: FormStorage, vararg val keyToRead: String) : FieldRule {
        override fun verify(value: FieldValue) = when (value) {
            is FieldValue.Bool -> {
                if (value.bool.equals(false)) {
                    keyToRead.any {
                        storage.getValue(it).asBool()!!.bool.equals(true)
                    }
                } else {
                    true
                }
            }
            else -> false
        }

        override fun observedKeys() = listOf(
                KeyReader(CHECKBOX_3_G1, storage),
                KeyReader(CHECKBOX_1_G1, storage),
                KeyReader(CHECKBOX_2_G1, storage))
    }
}

val SIMPLE_KEY_TO_VALUE = { key: String, value: FieldValue -> value.toNode() at key }
val KEY_TO_BOOL_AS_1_0 = { key: String, value: FieldValue -> (if (value.asBoolOrFalse() == true) "1" else "0").asNode() at "$key" }



