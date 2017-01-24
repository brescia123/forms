package it.facile.form.model

import it.facile.form.Dates
import it.facile.form.model.configurations.*
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.CHECKBOX
import it.facile.form.model.configurations.FieldConfigBool.ViewStyle.TOGGLE
import it.facile.form.model.models.FieldModel
import it.facile.form.model.models.FormModel
import it.facile.form.model.models.PageModel
import it.facile.form.model.models.SectionModel
import it.facile.form.model.representation.FieldRepresentation
import it.facile.form.model.representation.FieldRepresentationRule.NEVER
import it.facile.form.storage.FieldPossibleValues
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.storage.FormStorageApi
import it.facile.form.toSingle
import rx.Scheduler
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.DateFormat
import java.util.*

fun form(storage: FormStorageApi = FormStorage.empty(),
         actions: List<Pair<String, (FieldValue, FormStorageApi) -> Unit>> = emptyList(),
         workScheduler: Scheduler = Schedulers.io(),
         resultScheduler: Scheduler = AndroidSchedulers.mainThread(),
         init: FormModel.() -> Unit): FormModel {
    val form = FormModel(
            storage = storage,
            actions = actions.toMutableList(),
            workScheduler = workScheduler,
            resultScheduler = resultScheduler)
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

interface FieldBuilder {
    fun build(): FieldModel
}


/* ---------- EMPTY ---------- */

/** Type-safe builder method to add an empty field */
fun SectionModel.empty(label: String): FieldModel {
    val fieldModel = FieldModel(key = "", configuration = FieldConfigEmpty(label))
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- CHECKBOX ---------- */

class FieldCheckboxBuilder(private val key: String) : FieldBuilder {
    var label: String = ""
    var boolToStringConverter: ((Boolean) -> String) = { "" }
    var rules: (FormStorageApi) -> List<FieldRule> = { emptyList() }
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() =
            FieldModel(key, representation, FieldConfigBool(label, CHECKBOX, boolToStringConverter, rules))
}

/** Type-safe builder method to add a checkbox field */
fun SectionModel.checkbox(key: String, init: FieldCheckboxBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldCheckboxBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- TOGGLE ---------- */

class FieldToggleBuilder(private val key: String) : FieldBuilder {
    var label: String = ""
    var boolToStringConverter: ((Boolean) -> String) = { "" }
    var rules: (FormStorageApi) -> List<FieldRule> = { emptyList() }
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() =
            FieldModel(key, representation, FieldConfigBool(label, TOGGLE, boolToStringConverter, rules))
}

/** Type-safe builder method to add a toggle field */
fun SectionModel.toggle(key: String, init: FieldToggleBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldToggleBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- CUSTOM BEHAVIOUR ---------- */

class FieldCustomBehaviourBuilder(private val key: String, val behaviourId: String) : FieldBuilder {
    var label: String = ""
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() = FieldModel(key, representation, FieldConfigCustomBehaviour(label, behaviourId))
}

fun SectionModel.customBehaviour(key: String, customBehaviourId: String, init: FieldCustomBehaviourBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldCustomBehaviourBuilder(key, customBehaviourId).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- DEFERRED CONFIG ---------- */

class FieldDeferredBuilder(private val key: String) : FieldBuilder {
    var label: String = ""
    var deferredConfig: Single<FieldConfig> = FieldConfigEmpty(label).toSingle()
    var errorMessage: String = "Loading error"
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() =
            FieldModel(key, representation, FieldConfigDeferred(label, deferredConfig, errorMessage))
}

/** Type-safe builder method to add a deferred configuration field */
fun SectionModel.deferred(key: String, init: FieldDeferredBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldDeferredBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- INPUT TEXT ---------- */

class FieldInputTextBuilder(private val key: String) : FieldBuilder {
    var label: String = ""
    var rules: (FormStorageApi) -> List<FieldRule> = { emptyList() }
    var inputTextType: InputTextType = InputTextType.TEXT
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() =
            FieldModel(key, representation, FieldConfigInputText(label, rules, inputTextType))
}

/** Type-safe builder method to add an input text field */
fun SectionModel.input(key: String, init: FieldInputTextBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldInputTextBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- PICKER ---------- */

class FieldPickerBuilder(private val key: String) : FieldBuilder {
    var label: String = ""
    var possibleValues: FieldPossibleValues = FieldPossibleValues.Available(emptyList())
    var placeHolder: String = "Select a value"
    var errorMessage: String = "Loading error"
    var rules: (FormStorageApi) -> List<FieldRule> = { emptyList() }
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() =
            FieldModel(key, representation, FieldConfigPicker(label, possibleValues, placeHolder, errorMessage, rules))
}

/** Type-safe builder method to add a picker field */
fun SectionModel.picker(key: String, init: FieldPickerBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldPickerBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- PICKER CUSTOM ---------- */

class FieldPickerCustomBuilder(private val key: String, val customPickerId: String) : FieldBuilder {
    var label: String = ""
    var placeHolder: String = "Select a value"
    var rules: (FormStorageApi) -> List<FieldRule> = { emptyList() }
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() = FieldModel(key, representation, FieldConfigPickerCustom(label, customPickerId, placeHolder, rules))
}

/** Type-safe builder method to add a custom picker field */
fun SectionModel.pickerCustom(key: String, customPickerId: String, init: FieldPickerCustomBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldPickerCustomBuilder(key, customPickerId).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}

/* ---------- PICKER DATE ---------- */

class FieldConfigPickerDateBuilder(private val key: String) : FieldBuilder {
    var label: String = ""
    var minDate: Date = Dates.create(1900, 0, 1)
    var maxDate: Date = Dates.create(2100, 11, 31)
    var dateFormatter: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
    var placeHolder: String = "Select a date"
    var rules: (FormStorageApi) -> List<FieldRule> = { emptyList() }
    var representation: FieldRepresentation = FieldRepresentation(NEVER)
    override fun build() =
            FieldModel(key, representation, FieldConfigPickerDate(label, minDate, maxDate, dateFormatter, placeHolder, rules))
}

/** Type-safe builder method to add a date picker field */
fun SectionModel.pickerDate(key: String, init: FieldConfigPickerDateBuilder.() -> Unit): FieldModel {
    val fieldModel = FieldConfigPickerDateBuilder(key).apply(init).build()
    fields.add(fieldModel)
    return fieldModel
}