package it.facile.form.model

import it.facile.form.Dates
import it.facile.form.model.configurations.*
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.CHECKBOX
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.TOGGLE
import it.facile.form.model.models.FieldModel
import it.facile.form.model.models.FormModel
import it.facile.form.model.models.PageModel
import it.facile.form.model.models.SectionModel
import it.facile.form.model.serialization.FieldSerialization
import it.facile.form.model.serialization.FieldSerializationApi
import it.facile.form.model.serialization.FieldSerializationRule
import it.facile.form.model.serialization.FieldSerializationStrategy
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.toSingle
import rx.Single
import java.text.DateFormat
import java.util.*

fun form(storage: FormStorage = FormStorage.empty(), actions: List<Pair<String, (FieldValue, FormStorage) -> Unit>> = emptyList(), init: FormModel.() -> Unit): FormModel {
    val form = FormModel(storage, actions = actions.toMutableList())
    form.init()
    return form
}

/** Type-safe builder method to add a page */
fun FormModel.page(title: String, init: PageModel.() -> Unit): PageModel {
    val page = PageModel(title)
    page.init()
    pages.add(page)
    return page
}


/** Type-safe builder method to add a section */
fun PageModel.section(title: String, init: SectionModel.() -> Unit): SectionModel {
    val section = SectionModel(title)
    section.init()
    sections.add(section)
    return section
}

/** Type-safe builder method to add a field */
fun SectionModel.field(key: String,
          serialization: FieldSerialization = FieldSerialization(FieldSerializationRule.NEVER, FieldSerializationStrategy.None),
          config: FieldConfig): FieldModel {
    val field = FieldModel(key, serialization, config)
    fields.add(field)
    return field
}

fun SectionModel.fieldd(key: String, init: FieldModelBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldModelBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}


class FieldModelBuilder(val key: String) {
    var serialization: FieldSerializationApi = FieldSerialization(FieldSerializationRule.NEVER, FieldSerializationStrategy.None)
    var configuration: FieldConfigApi = FieldConfigEmpty("")
    fun build() = FieldModel(key, serialization, configuration)
}

class FieldConfigCheckboxBuilder(private val label: String) {
    var boolToStringConverter: ((Boolean) -> String) = { "" }
    var rules: (FormStorage) -> List<FieldRule> = { emptyList() }
    fun build() = FieldConfigBool(label, CHECKBOX, boolToStringConverter, rules)
}

class FieldConfigToggleBuilder(private val label: String) {
    var boolToStringConverter: ((Boolean) -> String) = { "" }
    var rules: (FormStorage) -> List<FieldRule> = { emptyList() }
    fun build() = FieldConfigBool(label, TOGGLE, boolToStringConverter, rules)
}

class FieldConfigPickerBuilder(private val label: String) {
    var possibleValues: FieldPossibleValues = FieldPossibleValues.Available(emptyList())
    var placeHolder: String = "Select a value"
    var errorMessage: String = "Loading error"
    var rules: (FormStorage) -> List<FieldRule> = { emptyList() }
    fun build() = FieldConfigPicker(label, possibleValues, placeHolder, errorMessage, rules)
}

class FieldConfigDeferredBuilder(private val label: String) {
    var deferredConfig: Single<FieldConfig> = FieldConfigEmpty(label).toSingle()
    var errorMessage: String = "Loading error"
    fun build() = FieldConfigDeferred(label, deferredConfig, errorMessage)
}

class FieldConfigInputTextBuilder(private val label: String) {
    var rules: (FormStorage) -> List<FieldRule> = { emptyList() }
    var inputTextType: InputTextType = InputTextType.TEXT
    fun build() = FieldConfigInputText(label, rules, inputTextType)
}

class FieldConfigPickerCustomBuilder(private val label: String, val id: String) {
    var placeHolder: String = "Select a value"
    var rules: (FormStorage) -> List<FieldRule> = { emptyList() }
    fun build() = FieldConfigPickerCustom(label, id, placeHolder, rules)
}

class FieldConfigPickerDateBuilder(private val label: String) {
    var minDate: Date = Dates.create(1900, 0, 1)
    var maxDate: Date = Dates.create(2100, 11, 31)
    var dateFormatter: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
    var placeHolder: String = "Select a date"
    var rules: (FormStorage) -> List<FieldRule> = { emptyList() }
    fun build() = FieldConfigPickerDate(label, minDate, maxDate, dateFormatter, placeHolder, rules)
}


/* Configurations */

fun FieldModelBuilder.empty(label: String) {
    configuration = FieldConfigEmpty(label)
}
fun FieldModelBuilder.checkbox(label: String, init: FieldConfigCheckboxBuilder.() -> Unit) {
    configuration = FieldConfigCheckboxBuilder(label).apply(init).build()
}
fun FieldModelBuilder.toggle(label: String, init: FieldConfigToggleBuilder.() -> Unit) {
    configuration = FieldConfigToggleBuilder(label).apply(init).build()
}
fun FieldModelBuilder.customBehaviour(label: String, customBehaviourId: String) {
    configuration = FieldConfigCustomBehaviour(label, customBehaviourId)
}
fun FieldModelBuilder.deferred(label: String, init: FieldConfigDeferredBuilder.() -> Unit) {
    configuration = FieldConfigDeferredBuilder(label).apply(init).build()
}
fun FieldModelBuilder.input(label: String, init: FieldConfigInputTextBuilder.() -> Unit) {
    configuration = FieldConfigInputTextBuilder(label).apply(init).build()
}
fun FieldModelBuilder.picker(label: String, init: FieldConfigPickerBuilder.() -> Unit) {
    configuration = FieldConfigPickerBuilder(label).apply(init).build()
}
fun FieldModelBuilder.customPicker(label: String, id: String, init: FieldConfigPickerCustomBuilder.() -> Unit) {
    configuration = FieldConfigPickerCustomBuilder(label, id).apply(init).build()
}
fun FieldModelBuilder.datePicker(label: String, init: FieldConfigPickerDateBuilder.() -> Unit) {
    configuration = FieldConfigPickerDateBuilder(label).apply(init).build()
}

/* Serialization */
fun FieldModelBuilder.serialization(init: () -> FieldSerializationApi) {
    serialization = init()
}
