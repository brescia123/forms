package it.facile.form

import io.kotlintest.properties.Gen
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldPossibleValues.ToBeRetrieved
import it.facile.form.storage.FieldValue
import it.facile.form.storage.keyTo
import java.util.*

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

object AvailablePossibleValueaGen : Gen<Available> {
    override fun generate() =
            Available((0..Random().nextInt(100)).map { Gen.string().generate() keyTo Gen.string().generate() })
}

object ToBeRetrievedPossibleValueaGen : Gen<ToBeRetrieved> {
    override fun generate() =
            ToBeRetrieved((0..Random().nextInt(100)).map { Gen.string().generate() keyTo Gen.string().generate() }.toSingle())
}

object FieldPossibleValuesGen : Gen<FieldPossibleValues> {
    override fun generate(): FieldPossibleValues =
            Gen.oneOf(listOf(ToBeRetrievedPossibleValueaGen, AvailablePossibleValueaGen))
                    .generate()
                    .generate()
}

class MapGenerator : Gen<Map<Any, Any>> {
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