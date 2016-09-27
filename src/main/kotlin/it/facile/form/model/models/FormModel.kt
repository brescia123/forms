package it.facile.form.model.models

import it.facile.form.logE
import it.facile.form.model.FieldRulesValidator
import it.facile.form.model.FieldsContainer
import it.facile.form.not
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldPath
import rx.Observable
import java.util.*

data class FormModel(val storage: FormStorage, val actions: HashMap<String, List<(FieldValue, FormStorage) -> Unit>>) : FieldsContainer {

    val pages = arrayListOf<PageModel>()
    val interestedKeys: MutableMap<String, MutableList<String>> by lazy { observeActionsKeys() }

    override fun fields(): List<FieldModel> = pages.fold(mutableListOf<FieldModel>(), { models, page ->
        models.addAll(page.fields())
        models
    })

    fun getPage(path: FieldPath): PageModel = pages[path.pageIndex]
    fun getSection(path: FieldPath): SectionModel = pages[path.pageIndex].sections[path.sectionIndex]
    fun getField(path: FieldPath): FieldModel = pages[path.pageIndex].sections[path.sectionIndex].fields[path.fieldIndex]

    fun observeChanges(): Observable<FieldPath> = storage.observe()
            .filter { contains(it) } // Filter if the model does not contain the field key
            .doOnNext { executeFieldAction(it) }
            .flatMap {
                val observable1 = Observable.just(it)
                val observable2 = Observable.from(interestedKeys[it] ?: emptyList())
                observable1.mergeWith(observable2)
            }
            .map { findFieldPathByKey(it) }
            .flatMap { Observable.from(it) } // Emit for every FieldPath related to the field key
            .doOnError { logE(it.message) }
            .retry() // Resubscribe if some errors occurs to continue the flow of notifications
            .map { it } // Used to deal with nullable Kotlin types in rxJava

    fun notifyValueChanged(path: FieldPath, value: FieldValue): Unit {
        if (not(contains(path))) return // The model does not contain the given path
        val key = findFieldModelByFieldPath(path).key
        storage.putValue(key, value)
    }

    /** Type-safe builder method to add a page */
    fun page(title: String, init: PageModel.() -> Unit): PageModel {
        val page = PageModel(title)
        page.init()
        pages.add(page)
        return page
    }

    private fun executeFieldAction(key: String) =
            actions[key]?.forEach { it(storage.getValue(key), storage) }


    private fun contains(path: FieldPath): Boolean = path.pageIndex < pages.size &&
            path.sectionIndex < pages[path.pageIndex].sections.size &&
            path.fieldIndex < pages[path.pageIndex].sections[path.sectionIndex].fields.size

    private fun contains(key: String): Boolean = findFieldPathByKey(key).size > 0

    private fun findFieldModelByFieldPath(fieldPath: FieldPath): FieldModel =
            pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].fields[fieldPath.fieldIndex]

    private fun findFieldPathByKey(key: String): List<FieldPath> = FieldPath.buildForKey(key, this)

    private fun executeAllFieldsActions() {
        fields().map { executeFieldAction(it.key) }
    }

    private fun observeActionsKeys(): MutableMap<String, MutableList<String>> {
        val interested: MutableMap<String, MutableList<String>> = mutableMapOf()
        for ((toBeNotifiedKey, config) in fields()) {
            if (config is FieldRulesValidator) {
                config.rules(storage).map {
                    it.observedKeys()
                            .map { it.key }
                            .map {
                                interested[it]?.add(toBeNotifiedKey) ?: interested.put(it, mutableListOf(toBeNotifiedKey))
                            }
                }
            }
        }
        return interested
    }

    companion object {
        fun form(storage: FormStorage, actions: HashMap<String, List<(FieldValue, FormStorage) -> Unit>>, init: FormModel.() -> Unit): FormModel {
            val form = FormModel(storage, actions)
            form.init()
            form.executeAllFieldsActions()
            return form
        }
    }
}