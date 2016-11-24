package it.facile.form.model

import io.kotlintest.mock.mock
import io.kotlintest.specs.ShouldSpec
import it.facile.form.IsEmail
import it.facile.form.NotMissing
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Text
import it.facile.form.storage.FormStorage
import it.facile.form.storage.FormStorageApi
import org.mockito.Mockito

class ModelInterfacesTest : ShouldSpec() {
    init {
        "FieldRulesValidator.isValid" {
            should("return first error message found") {
                val validator: FieldRulesValidator = object : FieldRulesValidator {
                    override val rules = { s: FormStorage ->
                        listOf(
                                NotMissing("missingError"),
                                IsEmail("emailError"))
                    }
                }

                validator.isValid(Missing, FormStorage(emptyMap())) shouldBe "missingError"
                validator.isValid(Text("s"), FormStorage(emptyMap())) shouldBe "emailError"
                validator.isValid(Text("bre@gma.com"), FormStorage(emptyMap())) shouldBe null
            }
        }

        "KeyReader.getValue" {
            should("call FormStorage.getValue with right key") {
                val storageMock: FormStorageApi = mock()
                KeyReader("key", storageMock).getValue()
                Mockito.verify(storageMock).getValue("key")
            }
        }

        "KeyReader.isHidden" {
            should("call FormStorage.isHidden with right key") {
                val storageMock: FormStorageApi = mock()
                KeyReader("key", storageMock).isHidden()
                Mockito.verify(storageMock).isHidden("key")
            }
        }

        "KeyReader.isDisabled" {
            should("call FormStorage.isDisabled() with right key") {
                val storageMock: FormStorageApi = mock()
                KeyReader("key", storageMock).isDisabled()
                Mockito.verify(storageMock).isDisabled("key")
            }
        }

        "KeyReader.getPossibleValues" {
            should("call FormStorage.getPossibleValues with right key") {
                val storageMock: FormStorageApi = mock()
                KeyReader("key", storageMock).getPossibleValues()
                Mockito.verify(storageMock).getPossibleValues("key")
            }
        }
    }
}

