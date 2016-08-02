package it.facile.form

import it.facile.form.model.FieldModel
import it.facile.form.model.FormModel
import it.facile.form.model.PageModel
import it.facile.form.model.SectionModel
import it.facile.form.model.configuration.FieldConfigBool
import it.facile.form.model.configuration.FieldConfigInputText
import it.facile.form.model.configuration.FieldConfigPicker
import it.facile.form.viewmodel.Describable
import it.facile.form.viewmodel.FieldValue

fun formModel(storage: FormStorage): FormModel {
    val objects = listOf(DummyDescribableK("Ciao"), DummyDescribableK("Ciao2"))
    val objectSingle = listOf(DummyDescribableK("CiaoSingle"))
    return FormModel(
            storage,
            PageModel(
                    "Page1",
                    SectionModel(
                            "Section1",
                            FieldModel(2, FieldConfigPicker("Label 2", objects, "Select a value")),
                            FieldModel(3, FieldConfigInputText("Label 3")),
                            FieldModel(4, FieldConfigBool("Label 4", FieldConfigBool.ViewStyle.CHECKBOX) { bool -> "unimplemented" })),
                    SectionModel(
                            "Section2",
                            FieldModel(5, FieldConfigInputText("Label 5")),
                            FieldModel(6, FieldConfigBool("Label 6", FieldConfigBool.ViewStyle.TOGGLE) { bool -> "unimplemented" }),
                            FieldModel(7, FieldConfigBool("Label 7", FieldConfigBool.ViewStyle.CHECKBOX) { bool -> "unimplemented" })),
                    SectionModel(
                            "Section3",
                            FieldModel(8, FieldConfigBool("Label 8", FieldConfigBool.ViewStyle.CHECKBOX) { bool -> "unimplemented" }),
                            FieldModel(9, FieldConfigBool("Label 9", FieldConfigBool.ViewStyle.CHECKBOX) { bool -> "unimplemented" }),
                            FieldModel(10, FieldConfigBool("Label 10", FieldConfigBool.ViewStyle.TOGGLE) { bool -> "unimplemented" }),
                            FieldModel(11, FieldConfigPicker("Label 11", objectSingle, "Select a value")))),
            PageModel(
                    "Page2",
                    SectionModel(
                            "Section1",
                            FieldModel(12, FieldConfigInputText("Label 12")),
                            FieldModel(13, FieldConfigInputText("Label 13"))
                    )
            ))
}

fun formStorage(): FormStorage = FormStorage(mutableMapOf(
        2 to FieldValue.Object(),
        3 to FieldValue.Text("Previous value"),
        4 to FieldValue.Bool(true),
        5 to FieldValue.Text(),
        6 to FieldValue.Bool(),
        7 to FieldValue.Bool(true),
        8 to FieldValue.Empty,
        9 to FieldValue.Bool(true),
        10 to FieldValue.Bool(true),
        11 to FieldValue.Object()
))

class DummyDescribableK(val title: String) : Describable {
    override fun describe(): String {
        return title
    }
}
