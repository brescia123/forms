package it.facile.form.model

import it.facile.form.FormStorageK
import it.facile.form.model.configuration.FieldConfigurationPickerK
import it.facile.form.viewmodel.FieldPathK
import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import rx.Observable
import rx.subjects.PublishSubject

class FormModelK(val storage: FormStorageK, vararg val pages: PageModelK) {

    val notifier: PublishSubject<Int> = PublishSubject.create()

    init {
        fields().mapIndexed { i, fieldModelK ->
            when (fieldModelK.fieldConfiguration) {
                is FieldConfigurationPickerK -> {
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

    fun observeChanges(): Observable<Pair<FieldPathK, FieldViewModelK>> {
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

    fun notifyValueChanged(fieldPath: FieldPathK, value: FieldValueK): Unit {
        if (contains(fieldPath) == false) return
        val key = findFieldModelByFieldPath(fieldPath).key
        storage.putValue(key, value)
    }

    fun fields(): List<FieldModelK> {
        return pages.fold(mutableListOf<FieldModelK>(), { models, page ->
            models.addAll(page.fields())
            models
        })
    }

    /* HELPERS */

    private fun contains(path: FieldPathK): Boolean {
        return path.pageIndex < pages.size &&
                path.sectionIndex < pages[path.pageIndex].sections.size &&
                path.fieldIndex < pages[path.pageIndex].sections[path.sectionIndex].fields.size
    }

    private fun findFieldModelByFieldPath(fieldPath: FieldPathK): FieldModelK {
        return pages[fieldPath.pageIndex].sections[fieldPath.sectionIndex].fields[fieldPath.fieldIndex]
    }

    private fun findFieldPathByKey(key: Int): FieldPathK? {
        return FieldPathK.Builder.buildForKey(key, this)
    }

    object Helpers {
        fun buildFieldViewModels(fields: List<FieldModelK>, formStorage: FormStorageK): List<FieldViewModelK> {
            return fields.fold(mutableListOf<FieldViewModelK>(), { viewModels, field ->
                viewModels.add(field.buildFieldViewModel(formStorage, false))
                viewModels
            })
        }
    }

}