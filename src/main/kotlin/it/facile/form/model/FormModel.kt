package it.facile.form.model

import it.facile.form.FormStorage
import it.facile.form.model.configuration.FieldConfigPicker
import it.facile.form.viewmodel.FieldPath
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import rx.Observable
import rx.subjects.PublishSubject

class FormModel(val storage: FormStorage) : FieldsContainer{

    val pages = arrayListOf<PageModel>()
    val notifier: PublishSubject<Int> = PublishSubject.create()

    init {
        fields().mapIndexed { i, fieldModelK ->
            when (fieldModelK.fieldConfiguration) {
                is FieldConfigPicker -> {
                    fieldModelK.fieldConfiguration.observe().subscribe(
                            { notifier.onNext(fieldModelK.key) },
                            { }
                    )
                }
                else -> {
                }
            }
        }
    }

    fun observeChanges(): Observable<Pair<FieldPath, FieldViewModel>> {
        return notifier
                .asObservable()
                .mergeWith(storage.observe())
                .filter { findFieldPathByKey(it) != null }
                .map {
                    val path = findFieldPathByKey(it)
                    path?.let {
                        val viewModel = findFieldModelByFieldPath(path).buildFieldViewModel(storage, false)
                        Pair(path, viewModel)
                    }
                }
    }

    fun notifyValueChanged(fieldPath: FieldPath, value: FieldValue): Unit {
        if (contains(fieldPath) == false) return
        val key = findFieldModelByFieldPath(fieldPath).key
        storage.putValue(key, value)
    }

    override fun fields(): List<FieldModel> {
        return pages.fold(mutableListOf<FieldModel>(), { models, page ->
            models.addAll(page.fields())
            models
        })
    }

    /** Type-safe builder method to add a page */
    fun page(title: String, init: PageModel.() -> Unit): PageModel {
        val page = PageModel(title)
        page.init()
        pages.add(page)
        return page
    }

    /* HELPERS */

    private fun contains(path: FieldPath): Boolean {
        return path.pageIndex < pages.size &&
                path.sectionIndex < pages[path.pageIndex].sections.size &&
                path.fieldIndex < pages[path.pageIndex].sections[path.sectionIndex].fields.size
    }

    private fun findFieldModelByFieldPath(fieldPath: FieldPath): FieldModel {
        return pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].fields[fieldPath.fieldIndex]
    }

    private fun findFieldPathByKey(key: Int): FieldPath? {
        return FieldPath.Builder.buildForKey(key, this)
    }

    companion object {
        fun form(storage: FormStorage, init: FormModel.() -> Unit) : FormModel {
            val form = FormModel(storage)
            form.init()
            return form
        }

        fun buildFieldViewModels(fields: List<FieldModel>, formStorage: FormStorage): List<FieldViewModel> {
            return fields.fold(mutableListOf<FieldViewModel>(), { viewModels, field ->
                viewModels.add(field.buildFieldViewModel(formStorage, false))
                viewModels
            })
        }
    }
}