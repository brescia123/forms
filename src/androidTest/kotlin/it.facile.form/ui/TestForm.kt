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
import rx.Single

/* ---------- MESSAGES ---------- */
private const val MISSING_TEXT = "Per cortesia seleziona il campo"
private const val LOADING_ERROR_TEXT = "Errore"
private const val INVALID_NAME = "Il campo Nome deve contenere un nome valido"
private const val INVALID_PHONE_TEXT = "Il campo Cellulare deve contenere un numero di telefono valido"
private const val INVALID_EMAIL_TEXT = "Il campo Email deve contenere una e-mail valida"
private const val ALMOST_ONE_TEXT = "Selezionare almeno uno dei campi"

class TestForm {

    companion object {
        const val TAG = "FORM"

        /* ---------- SOURCE IDs ---------- */
        const val SOURCE_ID_THANKYOU = 0
        const val SOURCE_ID_RESULTS = 0

        /* ---------- FIELDS KEYS ---------- */
        const val UTENZA = "type"
        // Persona fisica
        const val ANAGRAFICA_NOME = "nome"
        const val ANAGRAFICA_COGNOME = "cognome"
        const val ATTUALE_FORNITORE = "attuale_fornitore"
        const val NUOVA_FORNITURA = "nuova_fornitura"
        const val RESIDENTI_CASA = "residenti_casa"
        const val COMUNE_RESIDENZA = "comune_residenza"
        // Persona giuridica
        const val RAGIONE_SOCIALE = "ragioneSociale"
        // Common
        const val ANAGRAFICA_TELEFONO = "telefono"
        const val ANAGRAFICA_EMAIL = "email"
        const val PRIVACY_CHECK = "check_all_1"
        const val PRIVACY_DETAILS = "privacy_details"
        const val PRIVACY_DISCLOSURES = "privacy_disclosures"
        // Filters
        const val UTILIZZO_CUCINA = "utilizzo_cucina"
        const val UTILIZZO_RISCALDAMENTO = "utilizzo_riscaldamento"
        const val UTILIZZO_ACQUA_CALDA = "utilizzo_acqua_calda"
        const val METRATURA_CASA = "metratura_casa"
        const val PAGAMENTO_RID = "pagamento_rid"
        const val PAGAMENTO_BOLLETTINO = "pagamento_bollettino"

        //Privacy
        const val PRIVACY_1 = "PRIVACY_1"
        const val PRIVACY_2 = "PRIVACY_2"
        const val PRIVACY_3 = "PRIVACY_3"
        const val PRIVACY_4 = "PRIVACY_4"

        const val CUSTOM_PICKER_ID_COMUNE_RESIDENZA = "comune_residenza_custom_picker_id"


        /* ---------- FIELDS ACTIONS ---------- */
        val NUOVA_FORNITURA_ACTIONS = { value: FieldValue, storage: FormStorageApi ->
            storage.setVisibility(TestForm.ATTUALE_FORNITORE, hidden = value.asBoolOrFalse(), executeActions = true)
        }
        val ATTUALE_FORNITORE_ACTIONS = { value: FieldValue, storage: FormStorageApi ->
            if (storage.getValue(TestForm.NUOVA_FORNITURA).asBoolOrFalse()) // If NUOVA_FORNITURA is checked hide it
                storage.setVisibility(TestForm.ATTUALE_FORNITORE, hidden = true)
            else
                storage.setVisibility(TestForm.ATTUALE_FORNITORE, hidden = false)
        }


        val FORM_ACTIONS = listOf(
                TestForm.UTENZA to { value: FieldValue, storage: FormStorageApi ->
                    when (value.asObject()?.value?.key) {
                        UTENZA_POSSIBLE_VALUES[0].key -> {
                            // Show "Persona fisica" fields
                            storage.setVisibility(TestForm.ANAGRAFICA_NOME, hidden = false)
                            storage.setVisibility(TestForm.ANAGRAFICA_COGNOME, hidden = false)
                            storage.setVisibility(TestForm.ATTUALE_FORNITORE, hidden = false)
                            storage.setVisibility(TestForm.NUOVA_FORNITURA, hidden = false)
                            storage.setVisibility(TestForm.COMUNE_RESIDENZA, hidden = false)
                            storage.setVisibility(TestForm.RESIDENTI_CASA, hidden = false)
                            storage.setVisibility(TestForm.METRATURA_CASA, hidden = false)
                            storage.setVisibility(TestForm.UTILIZZO_CUCINA, hidden = false)
                            storage.setVisibility(TestForm.UTILIZZO_RISCALDAMENTO, hidden = false)
                            storage.setVisibility(TestForm.UTILIZZO_ACQUA_CALDA, hidden = false)
                            storage.setVisibility(TestForm.PAGAMENTO_RID, hidden = false)
                            storage.setVisibility(TestForm.PAGAMENTO_BOLLETTINO, hidden = false)
                            // Hide "Persona giuridica" fields
                            storage.setVisibility(TestForm.RAGIONE_SOCIALE, true)
                            // Apply NUOVA_FORNITURA actions (hide/show ATTUALE_FORNITORE)
                            NUOVA_FORNITURA_ACTIONS.invoke(storage.getValue(TestForm.NUOVA_FORNITURA), storage)
                        }
                        UTENZA_POSSIBLE_VALUES[1].key -> {
                            // Hide "Persona fisica" fields
                            storage.setVisibility(TestForm.ANAGRAFICA_NOME, hidden = true)
                            storage.setVisibility(TestForm.ANAGRAFICA_COGNOME, hidden = true)
                            storage.setVisibility(TestForm.ATTUALE_FORNITORE, hidden = true)
                            storage.setVisibility(TestForm.NUOVA_FORNITURA, hidden = true)
                            storage.setVisibility(TestForm.COMUNE_RESIDENZA, hidden = true)
                            storage.setVisibility(TestForm.RESIDENTI_CASA, hidden = true)
                            storage.setVisibility(TestForm.METRATURA_CASA, hidden = true)
                            storage.setVisibility(TestForm.UTILIZZO_CUCINA, hidden = true)
                            storage.setVisibility(TestForm.UTILIZZO_RISCALDAMENTO, hidden = true)
                            storage.setVisibility(TestForm.UTILIZZO_ACQUA_CALDA, hidden = true)
                            storage.setVisibility(TestForm.PAGAMENTO_RID, hidden = true)
                            storage.setVisibility(TestForm.PAGAMENTO_BOLLETTINO, hidden = true)
                            // Show "Persona giuridica" fields
                            storage.setVisibility(TestForm.RAGIONE_SOCIALE, hidden = false)
                        }
                        else -> Unit
                    }
                },
                TestForm.NUOVA_FORNITURA to NUOVA_FORNITURA_ACTIONS,
                TestForm.ATTUALE_FORNITORE to ATTUALE_FORNITORE_ACTIONS
        )

        /* ---------- POSSIBLE VALUES ---------- */
        val UTENZA_POSSIBLE_VALUES = listOf("a" keyTo "Privato", "b" keyTo "Business")

        val RESIDENTI_CASA_POSSIBLE_VALUES = listOf(
                1 keyTo "1",
                2 keyTo "2",
                3 keyTo "3",
                4 keyTo "4",
                5 keyTo "5 o +")

        val METRATURA_CASA_POSSIBLE_VALUES = listOf(
                1 keyTo "fino a 40",
                2 keyTo "da 40 a 60",
                3 keyTo "da 60 a 80",
                4 keyTo "da 80 a 100",
                5 keyTo "più di 100")

        val ATTUALE_FORNITORE_POSSIBLE_VALUES = listOf(
                1 keyTo "A2A",
                2 keyTo "Enel",
                4 keyTo "AIM",
                0 keyTo "Altro")

        /* ---------- DEFAULT ENTRIES ---------- */
        val DEFAULT_ENTRIES = mapOf(
                TestForm.UTENZA to Entry(FieldValue.Object(UTENZA_POSSIBLE_VALUES[0])),
                TestForm.RAGIONE_SOCIALE to Entry(FieldValue.Text(), hidden = true),
                TestForm.NUOVA_FORNITURA to Entry(FieldValue.Bool()),
                TestForm.UTILIZZO_CUCINA to Entry(FieldValue.Bool(true)),
                TestForm.UTILIZZO_RISCALDAMENTO to Entry(FieldValue.Bool(true)),
                TestForm.UTILIZZO_ACQUA_CALDA to Entry(FieldValue.Bool(true)),
                TestForm.PAGAMENTO_RID to Entry(FieldValue.Bool(true)),
                TestForm.PAGAMENTO_BOLLETTINO to Entry(FieldValue.Bool(true)),
                TestForm.RESIDENTI_CASA to Entry(FieldValue.Object(RESIDENTI_CASA_POSSIBLE_VALUES[2] /* 3 */)),
                TestForm.METRATURA_CASA to Entry(FieldValue.Object(METRATURA_CASA_POSSIBLE_VALUES[0] /* fino a 40 */)))


        val FORM_MODEL = { storage: FormStorage, workScheduler: Scheduler, resultScheduler: Scheduler ->
            form(storage, FORM_ACTIONS, workScheduler = workScheduler, resultScheduler = resultScheduler) {
                page("Gas Form") {
                    section("Parametri") {
                        picker(TestForm.UTENZA) {
                            label = "Utenza"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(UTENZA_POSSIBLE_VALUES)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.ALWAYS representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        toggle(TestForm.NUOVA_FORNITURA) {
                            label = "Nuova fornitura"
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                        }
                        picker(TestForm.ATTUALE_FORNITORE)
                        {
                            label = "Attuale fornitore"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(ATTUALE_FORNITORE_POSSIBLE_VALUES)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs { key, value -> value.asObjectKey().let { if (it != "0") ObjectNode().with(it, at = NodePath(key, "attualeFornitore")) else null } ?: Node.ObjectNode.empty() }
                        }
                        picker(TestForm.RESIDENTI_CASA) {
                            label = "Persone che vivono in casa"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(RESIDENTI_CASA_POSSIBLE_VALUES)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        picker(TestForm.METRATURA_CASA) {
                            label = "Metri quadri casa"
                            placeHolder = ""
                            possibleValues = FieldPossibleValues.Available(METRATURA_CASA_POSSIBLE_VALUES)
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                    }
                    section("Utilizzo del gas") {
                        checkbox(TestForm.UTILIZZO_CUCINA) {
                            label = "Cucina"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.UTILIZZO_CUCINA, TestForm.UTILIZZO_RISCALDAMENTO, TestForm.UTILIZZO_ACQUA_CALDA)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                        checkbox(TestForm.UTILIZZO_RISCALDAMENTO) {
                            label = "Riscaldamento"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.UTILIZZO_RISCALDAMENTO, TestForm.UTILIZZO_CUCINA, TestForm.UTILIZZO_ACQUA_CALDA)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                        checkbox(TestForm.UTILIZZO_ACQUA_CALDA) {
                            label = "Produzione acqua calda"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.UTILIZZO_ACQUA_CALDA, TestForm.UTILIZZO_RISCALDAMENTO, TestForm.UTILIZZO_CUCINA)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                    }
                    section("Modalità di pagamento") {
                        checkbox(TestForm.PAGAMENTO_RID) {
                            label = "RID"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.PAGAMENTO_RID, TestForm.PAGAMENTO_BOLLETTINO)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                        checkbox(TestForm.PAGAMENTO_BOLLETTINO) {
                            label = "Bollettino"
                            rules = { listOf(AlmostOneSelected(ALMOST_ONE_TEXT, storage, TestForm.PAGAMENTO_BOLLETTINO, TestForm.PAGAMENTO_RID)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs KEY_TO_BOOL_AS_1_0
                        }
                    }
                    section("Richiedente") {
                        /* --------- Persona Fisica --------- */
                        input(TestForm.ANAGRAFICA_NOME) {
                            label = "Nome"
                            inputTextType = InputTextType.CAP_WORDS
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsName(INVALID_NAME)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                        input(TestForm.ANAGRAFICA_COGNOME) {
                            label = "Cognome"
                            inputTextType = InputTextType.CAP_WORDS
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsName(INVALID_NAME)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                        pickerCustom(TestForm.COMUNE_RESIDENZA, TestForm.CUSTOM_PICKER_ID_COMUNE_RESIDENZA) {
                            label = "Comune di Residenza"
                            placeHolder = ""
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }

                        /* --------- Persona Giuridica --------- */

                        input(TestForm.RAGIONE_SOCIALE) {
                            label = "Ragione Sociale"
                            inputTextType = InputTextType.CAP_WORDS
                            rules = { listOf(NotMissing(MISSING_TEXT + label)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }

                        /* --------- Common --------- */

                        input(TestForm.ANAGRAFICA_TELEFONO) {
                            label = "Cellulare"
                            inputTextType = InputTextType.PHONE
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsCellularPhone(INVALID_PHONE_TEXT)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                        input(TestForm.ANAGRAFICA_EMAIL) {
                            label = "Email"
                            inputTextType = InputTextType.EMAIL
                            rules = { listOf(NotMissing(MISSING_TEXT + label), IsEmail(INVALID_EMAIL_TEXT)) }
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_VALUE
                        }
                    }
                    section("Privacy") {
                        checkbox(PRIVACY_1) {
                            label = "privacy check 1"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        checkbox(PRIVACY_2) {
                            label = "privacy check 2"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        checkbox(PRIVACY_3) {
                            label = "privacy check 3"
                            representation = FieldRepresentationRule.IF_VISIBLE representAs SIMPLE_KEY_TO_OBJECT_KEY
                        }
                        checkbox(PRIVACY_4) {
                            label = "privacy check 4"
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
                KeyReader(UTILIZZO_ACQUA_CALDA, storage),
                KeyReader(UTILIZZO_CUCINA, storage),
                KeyReader(UTILIZZO_RISCALDAMENTO, storage))
    }
}

val SIMPLE_KEY_TO_VALUE = { key: String, value: FieldValue -> value.toNode() at key }
val KEY_TO_BOOL_AS_1_0 = { key: String, value: FieldValue -> (if (value.asBoolOrFalse() == true) "1" else "0").asNode() at "$key" }



