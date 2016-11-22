package it.facile.form

import io.kotlintest.properties.Gen
import it.facile.form.model.FieldConfig
import it.facile.form.model.configurations.*
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.CHECKBOX
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.TOGGLE
import it.facile.form.model.models.FieldModel
import it.facile.form.model.models.SectionModel
import it.facile.form.model.serialization.*
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldPossibleValues.ToBeRetrieved
import it.facile.form.storage.FieldValue
import it.facile.form.storage.keyTo
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom


/* ---------- FieldValue ---------- */

object FieldValueBoolGen : Gen<FieldValue.Bool> {
    override fun generate() = FieldValue.Bool(Random().nextBoolean())
}

object FieldValueTextGen : Gen<FieldValue.Text> {
    override fun generate() = FieldValue.Text(Gen.string().generate())
}

object FieldValueDateValueGen : Gen<FieldValue.DateValue> {
    override fun generate() = FieldValue.DateValue(Date(Gen.choose(0, Dates.today().time).generate()))
}

object FieldValueMissingGen : Gen<FieldValue.Missing> {
    override fun generate() = FieldValue.Missing
}

object FieldValueObjectGen : Gen<FieldValue.Object> {
    override fun generate() = FieldValue.Object(Gen.string().generate() keyTo Gen.string().generate())
}

object FieldValueGen : Gen<FieldValue> {
    override fun generate(): FieldValue =
            Gen.oneOf(listOf(FieldValueBoolGen, FieldValueTextGen, FieldValueDateValueGen, FieldValueMissingGen, FieldValueObjectGen))
                    .generate()
                    .generate()
}

object AvailablePossibleValuesGen : Gen<Available> {
    override fun generate() =
            Available((0..Random().nextInt(100)).map { Gen.string().generate() keyTo Gen.string().generate() })
}

object ToBeRetrievedPossibleValueaGen : Gen<ToBeRetrieved> {
    override fun generate() =
            ToBeRetrieved((0..Random().nextInt(100)).map { Gen.string().generate() keyTo Gen.string().generate() }.toSingle())
}

object FieldPossibleValuesGen : Gen<FieldPossibleValues> {
    override fun generate(): FieldPossibleValues =
            Gen.oneOf(listOf(ToBeRetrievedPossibleValueaGen, AvailablePossibleValuesGen))
                    .generate()
                    .generate()
}


/* ---------- FieldConfig ---------- */

interface FieldConfigGen<T> : Gen<T>

val fieldConfigGenerators = listOf(
        FieldConfigBoolGen,
        FieldConfigCustomBehaviourGen,
        FieldConfigDeferredGen,
        FieldConfigInputTextGen,
        FieldConfigPickerGen,
        FieldConfigPickerCustomGen,
        FieldConfigPickerDateGen)

object FieldConfigBoolGen : FieldConfigGen<FieldConfigBool> {
    override fun generate(): FieldConfigBool =
            FieldConfigBool(
                    label = Gen.string().generate(),
                    viewStyle = if (Gen.bool().generate()) CHECKBOX else TOGGLE,
                    boolToString = { Gen.string().generate() })
}

object FieldConfigCustomBehaviourGen : FieldConfigGen<FieldConfigCustomBehaviour> {
    override fun generate(): FieldConfigCustomBehaviour =
            FieldConfigCustomBehaviour(
                    label = Gen.string().generate(),
                    customBehaviourId = Gen.string().generate())
}

object FieldConfigDeferredGen : FieldConfigGen<FieldConfigDeferred> {
    override fun generate(): FieldConfigDeferred =
            FieldConfigDeferred(
                    label = Gen.string().generate(),
                    deferredConfig = FieldConfigsGen.generate().toSingle(),
                    errorMessage = Gen.string().generate())
}

object FieldConfigInputTextGen : FieldConfigGen<FieldConfigInputText> {
    override fun generate(): FieldConfigInputText =
            FieldConfigInputText(label = Gen.string().generate())
}

object FieldConfigPickerGen : FieldConfigGen<FieldConfigPicker> {
    override fun generate(): FieldConfigPicker =
            FieldConfigPicker(
                    label = Gen.string().generate(),
                    possibleValues = FieldPossibleValuesGen.generate(),
                    placeHolder = Gen.string().generate(),
                    errorMessage = Gen.string().generate())
}

object FieldConfigPickerCustomGen : FieldConfigGen<FieldConfigPickerCustom> {
    override fun generate(): FieldConfigPickerCustom =
            FieldConfigPickerCustom(
                    label = Gen.string().generate(),
                    placeHolder = Gen.string().generate(),
                    id = Gen.string().generate())
}

object FieldConfigPickerDateGen : FieldConfigGen<FieldConfigPickerDate> {
    override fun generate(): FieldConfigPickerDate {
        val date1 = DateGenerator.generate()
        val date2 = DateGenerator.generate()
        return FieldConfigPickerDate(
                label = Gen.string().generate(),
                minDate = if (date1.before(date2)) date1 else date2,
                maxDate = if (date1.after(date2)) date1 else date2,
                dateFormatter = SimpleDateFormat(),
                placeHolder = Gen.string().generate())
    }
}


object FieldConfigsGen : Gen<FieldConfig> {
    override fun generate(): FieldConfig =
            Gen.oneOf(fieldConfigGenerators).generate().generate()
}


/* ---------- FieldSerialization ---------- */

object FieldSerializationGen : Gen<FieldSerializationApi> {
    override fun generate(): FieldSerializationApi {
        return FieldSerialization(
                rule = FieldSerializationRuleGen.generate(),
                strategy = FieldSerializationStrategyGen.generate())
    }
}

object FieldSerializationRuleGen : Gen<FieldSerializationRule> {
    override fun generate() = FieldSerializationRule.values()[Random().nextInt(FieldSerializationRule.values().size)]
}

object FieldSerializationStrategyGen : Gen<FieldSerializationStrategy> {
    override fun generate(): FieldSerializationStrategy {
        val choice = Random().nextInt(3)
        return when (choice) {
            0 -> FieldSerializationStrategy.None
            1 -> FieldSerializationStrategy.SingleKey()
            2 -> FieldSerializationStrategy.MultipleKey(listOf(FieldSerializer()))
            else -> FieldSerializationStrategy.None
        }
    }
}


/* ---------- FieldModel ---------- */

object FieldModelGen : Gen<FieldModel> {
    override fun generate(): FieldModel = FieldModel(
            key = Gen.string().generate(),
            configuration = FieldConfigsGen.generate(),
            serialization = FieldSerializationGen.generate())
}

object FieldModelListGen : Gen<List<FieldModel>> {
    override fun generate() = (0..Random().nextInt(25)).map { FieldModelGen.generate() }
}

object SectionModelGen : Gen<SectionModel> {
    override fun generate(): SectionModel {
        return SectionModel(
                title = Gen.string().generate(),
                fields = FieldModelListGen.generate().toMutableList())
    }
}

object SectionModelListGen : Gen<List<SectionModel>> {
    override fun generate() = (0..Random().nextInt(10)).map { SectionModelGen.generate() }
}


/* ---------- Various ---------- */

object DateGenerator : Gen<Date> {
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

object MapGenerator : Gen<Map<Any, Any>> {
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