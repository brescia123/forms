package it.facile.form

import io.kotlintest.specs.ShouldSpec
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.*

class BasicFieldRulesTest : ShouldSpec() {
    init {
        "NotMissing.verify" {
            val notMissingRule = NotMissing()

            should("return false is FieldValue is Missing") {
                notMissingRule.verify(FieldValue.Missing) shouldBe false
            }
            should("return false is FieldValue is empty string") {
                notMissingRule.verify(FieldValue.Text("")) shouldBe false
            }
            should("return true is FieldValue is not Missing or empty string") {
                forAll(FieldValueGen) { fieldValue: FieldValue ->
                    if (fieldValue != Missing && fieldValue.asText()?.text != "") {
                        notMissingRule.verify(fieldValue)
                    }
                    true
                }
            }
        }

        "IsName.verify" {
            val isNameRule = IsName()

            should("return false if there are more than three consecutive equal characters") {
                isNameRule.verify(Text("aaa")) shouldBe false
                isNameRule.verify(Text("aaaaaa")) shouldBe false
                isNameRule.verify(Text("iaaao")) shouldBe false
                isNameRule.verify(Text("fppp")) shouldBe false
                isNameRule.verify(Text("fpsssppp")) shouldBe false
                isNameRule.verify(Text("fpppppp")) shouldBe false
                isNameRule.verify(Text("faafaa")) shouldBe true
            }
            should("return false if there are only consonant (j is vocal)") {
                isNameRule.verify(Text("t")) shouldBe false
                isNameRule.verify(Text("tt")) shouldBe false
                isNameRule.verify(Text("rr")) shouldBe false
                isNameRule.verify(Text("ee")) shouldBe true
                isNameRule.verify(Text("aeiouj")) shouldBe true
                isNameRule.verify(Text("j")) shouldBe true
                isNameRule.verify(Text("tj")) shouldBe true
            }
            should("return false if there are non 'a-zòàùèéìíóáúäëïöü ' chars") {
                isNameRule.verify(Text(" - ")) shouldBe false
                isNameRule.verify(Text("/ ")) shouldBe false
                isNameRule.verify(Text("&")) shouldBe false
            }
            should("reuturn true with valid names") {
                forAll(arrayOf("Laura Farone",
                        "Felipa Roselli",
                        "Emerald Kuchta",
                        "Ma Blowe",
                        "Shondra Normandin",
                        "Cherly Diedrich",
                        "Asuncion Such",
                        "Marylin Phaneuf",
                        "Antonetta Craighead",
                        "Babara Bakos",
                        "Krystle Brainard",
                        "Jolynn Caesar",
                        "Eloise Tomas",
                        "Anderson Hedgecock",
                        "Wendi Gaeth",
                        "Carey Kaiser",
                        "Domitila Esses",
                        "Yuk Aguirre",
                        "Pearl Ertle",
                        "Rudolph Coberly",
                        "Pam Kinard",
                        "Vashti Tyre",
                        "Mari Barley",
                        "Rosalba Sorenson",
                        "Naoma Heyd",
                        "Dorethea Gaskill",
                        "Darline Hertzler",
                        "Rupert Hepburn",
                        "Tillie Vidales",
                        "Jeanna Doolan",
                        "Winford Robichaud",
                        "Linn Monica",
                        "Reid Metivier",
                        "Tyson Bryon",
                        "Saul Dumas",
                        "Santana Balogh",
                        "Reita Bevans",
                        "Edda Hinchey",
                        "Dominica Sheets",
                        "Sondra Deweese",
                        "Damaris Pastore",
                        "Nadine Spradling",
                        "Victorina Kibby",
                        "Geri Washington",
                        "Harriet Gladfelter",
                        "Kara Thorn",
                        "Kathi Nealey",
                        "Isaac Lautenschlage",
                        "Olimpia Ricciardi",
                        "Le Masek"
                )) {
                    isNameRule.verify(Text(it)) shouldBe true
                }
            }
        }

        "ShouldBeTrue.verify" {
            val shouldBeTrueRule = ShouldBeTrue()
            should("return correct bool value") {
                shouldBeTrueRule.verify(Bool(true)) shouldBe true
                shouldBeTrueRule.verify(Bool(false)) shouldBe false
            }
            should("return false if not Bool") {
                forAll(FieldValueGen) {
                    if (it is Bool) true
                    else shouldBeTrueRule.verify(it) == false
                }
            }
        }

        "IsCellularPhone.verify" {
            val isCellularPhoneRule = IsCellularPhone()
            should("return true if valid phone") {
                forAll(arrayOf(
                        "3331924844",
                        "3491234533",
                        "332123453",
                        "3321234534",
                        "3201234534"
                )) {
                    isCellularPhoneRule.verify(Text(it))
                }
            }
            should("return true if invalid phone") {
                forAll(arrayOf(
                        "2331924844",
                        "349123453333",
                        "332153",
                        "",
                        "ds"
                )) {
                    isCellularPhoneRule.verify(Text(it))
                }
            }
        }

        "IsEmail.verify" {
            val isEmailRule = IsEmail()
            should("return true if valid email") {
                forAll(arrayOf(
                        "bre@gma.com",
                        "email@gmail.com",
                        "12email@test.it"
                )) {
                    isEmailRule.verify(Text(it))
                }
            }
            should("return true if invalid email") {
                forAll(arrayOf(
                        "",
                        "bre@@gm.co",
                        "em@gm.com.ee",
                        "em@gm",
                        "ds"
                )) {
                    isEmailRule.verify(Text(it))
                }
            }
        }
    }
}