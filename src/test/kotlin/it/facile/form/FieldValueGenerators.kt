package it.facile.form

import io.kotlintest.properties.Gen
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

