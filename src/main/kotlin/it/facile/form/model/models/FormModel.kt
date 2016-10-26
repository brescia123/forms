package it.facile.form.model.models

import it.facile.form.logD
import it.facile.form.logE
import it.facile.form.model.FieldConfig
import it.facile.form.model.FieldRulesValidator
import it.facile.form.model.FieldsContainer
import it.facile.form.model.configurations.FieldConfigDeferred
import it.facile.form.not
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.ui.viewmodel.FieldPath
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

data class FormModel(val storage: FormStorage,
                     val pages: ArrayList<PageModel> = arrayListOf<PageModel>(),
                     private val actions: MutableList<Pair<String, (FieldValue, FormStorage) -> Unit>>) : FieldsContainer {

    private val interestedKeys: MutableMap<String, MutableList<String>> by lazy { observeActionsKeys() }

    override fun fields(): List<FieldModel> = pages.fold(mutableListOf<FieldModel>(), { models, page ->
        models.addAll(page.fields())
        models
    })

    fun getPage(path: FieldPath): PageModel = pages[path.pageIndex]
    fun getSection(path: FieldPath): SectionModel = pages[path.pageIndex].sections[path.sectionIndex]
    fun getField(path: FieldPath): FieldModel = pages[path.pageIndex].sections[path.sectionIndex].fields[path.fieldIndex]

    fun observeChanges(): Observable<FieldPath> = storage.observe()
            .doOnNext { if (it.second) executeFieldAction(it.first) } // Execute all side effects actions related to the key if user made
            .map { it.first } // Get rid of userMade boolean information
            .flatMap { Observable.just(it).mergeWith(Observable.from(interestedKeys[it] ?: emptyList())) } // Merge with interested keys
            .flatMap { Observable.from(findFieldPathByKey(it)) } // Emit for every FieldPath associated to the field key
            .doOnError { logE(it.message) } // Log errors
            .retry() // Resubscribe if some errors occurs to continue the flow of notifications

    /** Notify the model of a field value change generated from the outside (that is a user made
     * change and not for the example one result of an field Action) */
    fun notifyValueChanged(path: FieldPath, value: FieldValue): Unit {
        if (not(contains(path))) return // The model does not contain the given path
        val key = findFieldModelByFieldPath(path).key
        storage.putValue(key, value, true)
    }

    /** Type-safe builder method to add a page */
    fun page(title: String, init: PageModel.() -> Unit): PageModel {
        val page = PageModel(title)
        page.init()
        pages.add(page)
        return page
    }

    private fun executeFieldAction(key: String) =
            actions.filter { it.first == key }.forEach { it.second(storage.getValue(key), storage) }


    private fun contains(path: FieldPath): Boolean = path.pageIndex < pages.size &&
            path.sectionIndex < pages[path.pageIndex].sections.size &&
            path.fieldIndex < pages[path.pageIndex].sections[path.sectionIndex].fields.size

    private fun contains(key: String): Boolean = findFieldPathByKey(key).size > 0

    private fun findFieldModelByFieldPath(fieldPath: FieldPath): FieldModel =
            pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].fields[fieldPath.fieldIndex]

    private fun findFieldPathByKey(key: String): List<FieldPath> = FieldPath.buildForKey(key, this)

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

    private fun replaceConfig(key: String, newConfig: FieldConfig) {
        val paths = findFieldPathByKey(key)
        paths.map {
            pages[it.pageIndex]
                    .sections[it.sectionIndex]
                    .fields[it.fieldIndex] = FieldModel(key, newConfig)
        }
    }

    private fun loadDeferredConfigs() {
        for ((key, config) in fields()) {
            if (config is FieldConfigDeferred) {
                logD("Loading  deferred config at key: $key")
                config.deferredConfig
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { // Replace config with the loaded one and notify
                                    replaceConfig(key, it)
                                    storage.ping(key)
                                    logD("Config at key $key changed")
                                },
                                { // Make config show the error and notify
                                    config.hasLoadingErrors = true
                                    storage.ping(key)
                                    logE(it)
                                }
                        )
            }
        }
    }

    companion object {
        fun form(storage: FormStorage, actions: List<Pair<String, (FieldValue, FormStorage) -> Unit>>, init: FormModel.() -> Unit): FormModel {
            val form = FormModel(storage, actions = actions.toMutableList())
            form.init()
            form.loadDeferredConfigs()
            return form
        }
    }

    fun addAction(pair: Pair<String, (FieldValue, FormStorage) -> Unit>) {
        actions.add(pair)
    }
}