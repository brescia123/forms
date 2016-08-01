package it.facile.form

import it.facile.form.model.FieldModelK
import it.facile.form.model.FormModelK
import it.facile.form.model.PageModelK
import it.facile.form.model.SectionModelK
import it.facile.form.model.configuration.FieldConfigurationBoolK
import it.facile.form.model.configuration.FieldConfigurationInputTextK
import it.facile.form.model.configuration.FieldConfigurationPickerK
import it.facile.form.viewmodel.DescribableK
import it.facile.form.viewmodel.FieldValueK

fun formModel(storage: FormStorageK): FormModelK {
    val objects = listOf(DummyDescribableK("Ciao"), DummyDescribableK("Ciao2"))
    val objectSingle = listOf(DummyDescribableK("CiaoSingle"))
    return FormModelK(
            storage,
            PageModelK(
                    "Page1",
                    SectionModelK(
                            "Section1",
                            FieldModelK(2, FieldConfigurationPickerK("Label 2", objects, "Select a value")),
                            FieldModelK(3, FieldConfigurationInputTextK("Label 3")),
                            FieldModelK(4, FieldConfigurationBoolK("Label 4", FieldConfigurationBoolK.ViewStyle.CHECKBOX) { bool -> "unimplemented" })),
                    SectionModelK(
                            "Section2",
                            FieldModelK(5, FieldConfigurationInputTextK("Label 5")),
                            FieldModelK(6, FieldConfigurationBoolK("Label 6", FieldConfigurationBoolK.ViewStyle.TOGGLE) { bool -> "unimplemented" }),
                            FieldModelK(7, FieldConfigurationBoolK("Label 7", FieldConfigurationBoolK.ViewStyle.CHECKBOX) { bool -> "unimplemented" })),
                    SectionModelK(
                            "Section3",
                            FieldModelK(8, FieldConfigurationBoolK("Label 8", FieldConfigurationBoolK.ViewStyle.CHECKBOX) { bool -> "unimplemented" }),
                            FieldModelK(9, FieldConfigurationBoolK("Label 9", FieldConfigurationBoolK.ViewStyle.CHECKBOX) { bool -> "unimplemented" }),
                            FieldModelK(10, FieldConfigurationBoolK("Label 10", FieldConfigurationBoolK.ViewStyle.TOGGLE) { bool -> "unimplemented" }),
                            FieldModelK(11, FieldConfigurationPickerK("Label 11", objectSingle, "Select a value")))),
            PageModelK(
                    "Page2",
                    SectionModelK(
                            "Section1",
                            FieldModelK(12, FieldConfigurationInputTextK("Label 12")),
                            FieldModelK(13, FieldConfigurationInputTextK("Label 13"))
                    )
            ))
}

fun formStorage(): FormStorageK = FormStorageK(mutableMapOf(
        2 to FieldValueK.Object(),
        3 to FieldValueK.Text("Previous value"),
        4 to FieldValueK.Bool(true),
        5 to FieldValueK.Text(),
        6 to FieldValueK.Bool(),
        7 to FieldValueK.Bool(true),
        8 to FieldValueK.Empty,
        9 to FieldValueK.Bool(true),
        10 to FieldValueK.Bool(true),
        11 to FieldValueK.Object()
))

class DummyDescribableK(val title: String) : DescribableK {
    override fun describe(): String {
        return title
    }
}
