package it.facile.form.model

import io.kotlintest.properties.Gen
import io.kotlintest.specs.ShouldSpec
import it.facile.form.CustomGen
import it.facile.form.model.representation.FieldRepresentation
import it.facile.form.model.representation.FieldRepresentationRule.IF_VISIBLE
import it.facile.form.model.representation.FieldRepresentationRule.NEVER

class RepresentationTest : ShouldSpec() {
    init {

        "FieldRepresentation.build" {
            should("return null if rule is NEVER") {
                forAll(Gen.string(), CustomGen.formStorage()) { key, storage ->
                    val representation = FieldRepresentation(NEVER)
                    representation.build(key, storage) == null
                }
            }
            should("return null if rule is IF_VISIBLE and the field is hidden") {
                forAll(Gen.string(), CustomGen.formStorage()) { key, storage ->
                    val representation = FieldRepresentation(IF_VISIBLE)
                    storage.setVisibility(key, hidden = true)
                    representation.build(key, storage) == null
                }
            }
            should("return null if strategy is None") {
                forAll(CustomGen.fieldRepresentationRule(), Gen.string(), CustomGen.formStorage()) { rule, key, storage ->
                    val representation = FieldRepresentation(rule)
                    representation.build(key, storage) == null
                }
            }
        }
    }
}