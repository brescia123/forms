package it.facile.form.model

import android.util.Log
import it.facile.form.FormStorage
import it.facile.form.model.configuration.DeferredConfig
import it.facile.form.viewmodel.FieldPath
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import rx.Observable
import java.util.*

class FormModel(val storage: FormStorage, val actions: HashMap<Int, List<FieldAction>>) : FieldsContainer {

    val pages = arrayListOf<PageModel>()

    override fun fields(): List<FieldModel> = pages.fold(mutableListOf<FieldModel>(), { models, page ->
        models.addAll(page.fields())
        models
    })

    fun observeChanges(): Observable<Pair<FieldPath, FieldViewModel>> = storage.observe()
            .filter { contains(it) } // Filter if the model does not contain the field key
            .doOnNext { executeFieldAction(it) }
            .map { keyToFieldPathAndViewModel(it) }
            .doOnError { Log.e(TAG, it.message) }
            .retry() // Resubscribe if some errors occurs to continue the flow of notifications
            .map { it } // Used to deal with nullable kotlin types in rxJava

    fun notifyValueChanged(path: FieldPath, value: FieldValue): Unit {
        if (contains(path) == false || value.equals(findFieldModelByFieldPath(path))) return
        val key = findFieldModelByFieldPath(path).key
        storage.putValue(key, value)
    }

    fun notifyValueChanged(key: Int, value: FieldValue): Unit {
        if (contains(key) == false) return
        storage.putValue(key, value)
    }

    /** Type-safe builder method to add a page */
    fun page(title: String, init: PageModel.() -> Unit): PageModel {
        val page = PageModel(title)
        page.init()
        pages.add(page)
        return page
    }

    private fun keyToFieldPathAndViewModel(key: Int): Pair<FieldPath, FieldViewModel>? = findFieldPathByKey(key)?.let {
        val viewModel = findFieldModelByFieldPath(it).buildFieldViewModel(storage)
        Log.d(TAG, "FieldModel ($key) -> $it - $viewModel")
        Pair(it, viewModel)
    } ?: null

    private fun executeFieldAction(key: Int) =
            actions[key]?.forEach { it.execute(storage.getValue(key), storage) }


    private fun contains(path: FieldPath): Boolean = path.pageIndex < pages.size &&
            path.sectionIndex < pages[path.pageIndex].sections.size &&
            path.fieldIndex < pages[path.pageIndex].sections[path.sectionIndex].fields.size

    private fun contains(key: Int): Boolean = findFieldPathByKey(key) != null

    private fun findFieldModelByFieldPath(fieldPath: FieldPath): FieldModel =
            pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].fields[fieldPath.fieldIndex]

    private fun findFieldPathByKey(key: Int): FieldPath? = FieldPath.Builder.buildForKey(key, this)

    private fun observeDeferredConfigs() =
            fields().map { fieldModel ->
                if (fieldModel.fieldConfiguration is DeferredConfig) {
                    fieldModel.fieldConfiguration.observe().subscribe(
                            { storage.notify(fieldModel.key) },
                            { Log.e("FormModel", it.message) }
                    )
                }
            }

    companion object {
        private val TAG: String = "FormModel"

        fun form(storage: FormStorage, actions: HashMap<Int, List<FieldAction>>, init: FormModel.() -> Unit): FormModel {
            val form = FormModel(storage, actions)
            form.init()
            form.fields().map { form.executeFieldAction(it.key) }
            form.observeDeferredConfigs()
            return form
        }
    }
}