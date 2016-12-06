package it.facile.form

import io.kotlintest.properties.Gen
import it.facile.form.model.FieldConfig
import it.facile.form.model.configurations.*
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.CHECKBOX
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.TOGGLE
import it.facile.form.model.models.FieldModel
import it.facile.form.model.models.FormModel
import it.facile.form.model.models.PageModel
import it.facile.form.model.models.SectionModel
import it.facile.form.model.serialization.*
import it.facile.form.storage.*
import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldPossibleValues.ToBeRetrieved
import it.facile.form.ui.viewmodel.FieldPath
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom

interface CustomGen {
    companion object {


        /* ---------- FieldModel ---------- */

        fun fieldModel() = object : Gen<FieldModel> {
            override fun generate(): FieldModel = FieldModel(
                    key = Gen.string().generate(),
                    configuration = fieldConfig().generate(),
                    serialization = fieldSerialization().generate())
        }

        fun fieldModelList() = object : Gen<List<FieldModel>> {
            override fun generate() = (0..Random().nextInt(25)).map { fieldModel().generate() }
        }

        fun sectionModel() = object : Gen<SectionModel> {
            override fun generate(): SectionModel {
                return SectionModel(
                        title = Gen.string().generate(),
                        fields = fieldModelList().generate().toMutableList())
            }
        }

        fun sectionModelList() = object : Gen<List<SectionModel>> {
            override fun generate() = (0..Random().nextInt(10)).map { sectionModel().generate() }
        }

        fun pageModel() = object : Gen<PageModel> {
            override fun generate(): PageModel {
                return PageModel(
                        title = Gen.string().generate(),
                        sections = sectionModelList().generate().toMutableList())
            }
        }

        fun pageModelList() = object : Gen<List<PageModel>> {
            override fun generate() = (0..Random().nextInt(10)).map { pageModel().generate() }
        }


        fun formModel(storage: FormStorage = formStorage().generate()) = object : Gen<FormModel> {
            override fun generate() = FormModel(storage, pageModelList().generate().toMutableList(), mutableListOf())
        }


        /* ---------- FieldPath ---------- */

        fun fieldPath(formModel: FormModel = Companion.formModel().generate(), contained: Boolean = true) = object : Gen<FieldPath> {
            override fun generate(): FieldPath {
                val random = Random()
                val i = random.nextInt(2)
                var pageIndex = random.nextInt(formModel.pages.lastIndex)
                var sectionIndex = random.nextInt(formModel.pages[pageIndex].sections.lastIndex)
                var fieldIndex = random.nextInt(formModel.pages[pageIndex].sections[sectionIndex].fields.lastIndex)
                if (!contained) {
                    when (i) {
                        0 -> pageIndex++
                        1 -> sectionIndex++
                        2 -> fieldIndex++
                    }
                }
                return FieldPath(fieldIndex, sectionIndex, pageIndex)
            }
        }

        /* ---------- FieldValue ---------- */

        fun fieldValueBool() = object : Gen<FieldValue.Bool> {
            override fun generate() = FieldValue.Bool(Random().nextBoolean())
        }

        fun fieldValueText() = object : Gen<FieldValue.Text> {
            override fun generate() = FieldValue.Text(Gen.string().generate())
        }

        fun fieldValueDateValue() = object : Gen<FieldValue.DateValue> {
            override fun generate() = FieldValue.DateValue(Date(Gen.choose(0, Dates.today().time).generate()))
        }

        fun fieldValueMissing() = object : Gen<FieldValue.Missing> {
            override fun generate() = FieldValue.Missing
        }

        fun fieldValueObject() = object : Gen<FieldValue.Object> {
            override fun generate() = FieldValue.Object(Gen.string().generate() keyTo Gen.string().generate())
        }

        fun fieldValue() = object : Gen<FieldValue> {
            override fun generate(): FieldValue = Gen.oneOf(listOf(fieldValueBool(), fieldValueText(), fieldValueDateValue(), fieldValueMissing(), fieldValueObject())).generate().generate()
        }


        /* ---------- FieldPossibleValues ---------- */

        fun possibleValuesAvailable() = object : Gen<Available> {
            override fun generate() = Available((0..Random().nextInt(100)).map { Gen.string().generate() keyTo Gen.string().generate() })
        }

        fun possibleValuesToBeRetrieved() = object : Gen<ToBeRetrieved> {
            override fun generate() = ToBeRetrieved((0..Random().nextInt(100)).map { Gen.string().generate() keyTo Gen.string().generate() }.toSingle())
        }

        fun possibleValues() = object : Gen<FieldPossibleValues> {
            override fun generate(): FieldPossibleValues = Gen.oneOf(listOf(possibleValuesAvailable(), possibleValuesToBeRetrieved())).generate().generate()
        }


        /* ---------- FieldConfig ---------- */

        interface FieldConfigGen<T> : Gen<T>

        val fieldConfigGenerators = listOf(fieldConfigBool(),
                fieldConfigCustomBehaviour(),
                fieldConfigDeferred(),
                fieldConfigInputText(),
                fieldConfigPicker(),
                fieldConfigPickerCustom(),
                fieldConfigPickerDate())

        fun fieldConfigBool(): FieldConfigGen<FieldConfigBool> = object : FieldConfigGen<FieldConfigBool> {
            override fun generate() = FieldConfigBool(
                    label = Gen.string().generate(),
                    viewStyle = if (Gen.bool().generate()) CHECKBOX else TOGGLE,
                    boolToString = { Gen.string().generate() })
        }

        fun fieldConfigCustomBehaviour(): FieldConfigGen<FieldConfigCustomBehaviour> = object : FieldConfigGen<FieldConfigCustomBehaviour> {
            override fun generate() = FieldConfigCustomBehaviour(
                    label = Gen.string().generate(),
                    customBehaviourId = Gen.string().generate())
        }

        fun fieldConfigDeferred(): FieldConfigGen<FieldConfigDeferred> = object : FieldConfigGen<FieldConfigDeferred> {
            override fun generate() = FieldConfigDeferred(
                    label = Gen.string().generate(),
                    deferredConfig = fieldConfig().generate().toSingle(),
                    errorMessage = Gen.string().generate())
        }

        fun fieldConfigInputText(): FieldConfigGen<FieldConfigInputText> = object : FieldConfigGen<FieldConfigInputText> {
            override fun generate() = FieldConfigInputText(label = Gen.string().generate())
        }

        fun fieldConfigPicker(): FieldConfigGen<FieldConfigPicker> = object : FieldConfigGen<FieldConfigPicker> {
            override fun generate() = FieldConfigPicker(
                    label = Gen.string().generate(),
                    possibleValues = possibleValues().generate(),
                    placeHolder = Gen.string().generate(),
                    errorMessage = Gen.string().generate())
        }

        fun fieldConfigPickerCustom(): FieldConfigGen<FieldConfigPickerCustom> = object : FieldConfigGen<FieldConfigPickerCustom> {
            override fun generate() = FieldConfigPickerCustom(
                    label = Gen.string().generate(),
                    placeHolder = Gen.string().generate(),
                    id = Gen.string().generate())
        }

        fun fieldConfigPickerDate(): FieldConfigGen<FieldConfigPickerDate> = object : FieldConfigGen<FieldConfigPickerDate> {
            override fun generate(): FieldConfigPickerDate {
                val date1 = dateGenerator().generate()
                val date2 = dateGenerator().generate()
                return FieldConfigPickerDate(
                        label = Gen.string().generate(),
                        minDate = if (date1.before(date2)) date1 else date2,
                        maxDate = if (date1.after(date2)) date1 else date2,
                        dateFormatter = SimpleDateFormat(),
                        placeHolder = Gen.string().generate())
            }
        }

        fun fieldConfig() = object : Gen<FieldConfig> {
            override fun generate(): FieldConfig = Gen.oneOf(fieldConfigGenerators).generate().generate()
        }


        /* ---------- FieldSerialization ---------- */

        fun fieldSerialization() = object : Gen<FieldSerializationApi> {
            override fun generate() = FieldSerialization(
                    rule = fieldSerializationRule().generate(),
                    strategy = fieldSerializationStrategy().generate())
        }

        fun fieldSerializationRule() = object : Gen<FieldSerializationRule> {
            override fun generate() = FieldSerializationRule.values()[Random().nextInt(FieldSerializationRule.values().size)]
        }

        fun fieldSerializationStrategy() = object : Gen<FieldSerializationStrategy> {
            override fun generate(): FieldSerializationStrategy = when (Random().nextInt(3)) {
                0 -> FieldSerializationStrategy.None
                1 -> FieldSerializationStrategy.SingleKey()
                2 -> FieldSerializationStrategy.MultipleKey(listOf(FieldSerializer()))
                else -> FieldSerializationStrategy.None
            }
        }


        /* ---------- FormStorage ---------- */

        fun formStorage() = object : Gen<FormStorage> {
            override fun generate() = FormStorage((0..Random().nextInt(20))
                    .map { Gen.string().generate() to Entry(fieldValue().generate(), Gen.bool().generate(), Gen.bool().generate()) }.toMap())
        }


        /* ---------- RemoteKey ---------- */

        fun remoteKey() = object : Gen<RemoteKey> {
            override fun generate() = RemoteKey(*(0..Random().nextInt(5)).map { Gen.string().generate() }.toTypedArray())
        }


        /* ---------- Various ---------- */

        fun dateGenerator() = object : Gen<Date> {
            override fun generate(): Date {
                val year = ThreadLocalRandom.current().nextInt(1980, 2080)
                val month = ThreadLocalRandom.current().nextInt(0, 12)
                val dayUpperBound = if (month == 1) 29
                else if (month == 10 || month == 3 || month == 5 || month == 8) 31
                else 32
                val day = ThreadLocalRandom.current().nextInt(1, dayUpperBound)
                return Dates.create(year, month, day)
            }
        }

        fun map() = object : Gen<Map<Any, Any>> {
            override fun generate(): Map<Any, Any> {
                val map = mutableMapOf<Any, Any>()
                val keyGen = Gen.oneOf(listOf(Gen.bool(), Gen.double(), Gen.float(), Gen.int(), Gen.long(), Gen.string()))
                val valueGen = Gen.oneOf(listOf(Gen.bool(), Gen.double(), Gen.float(), Gen.int(), Gen.long(), Gen.string()))
                for (i in 0..Random().nextInt(100)) {
                    map.put(keyGen.generate().generate(), valueGen.generate().generate())
                }
                return map
            }
        }
    }
}