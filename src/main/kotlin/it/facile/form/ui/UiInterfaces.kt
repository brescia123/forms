package it.facile.form.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import it.facile.form.model.CustomPickerId
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FormStorage
import it.facile.form.ui.adapters.FieldsLayouts
import it.facile.form.ui.adapters.SectionsAdapter
import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.*
import it.facile.form.ui.viewmodel.PageViewModel
import rx.Observable

/** Represent a Field that can show an error state */
interface CanShowError {
    fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean)

    fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean
}

/** Represent a Field that is interactive and so can notify new values */
interface CanNotifyNewValues {
    fun notifyNewValue(position: Int, newValue: FieldValue)
}

interface WithOriginalHeight {
    var originalHeight: Int
}


/** Represent a Field that can be hidden by reducing its height to 0 */
interface CanBeHidden {
    fun hide(itemView: View, originalHeight: Int, isHidden: Boolean) {
        val param = itemView.layoutParams
        if (isHidden) {
            param.height = 0
        } else {
            param.height = originalHeight
        }
        itemView.layoutParams = param
    }
}

interface ViewModel {
    fun isHidden(): Boolean
}

interface Visitable {
    fun viewType(viewTypeFactory: ViewTypeFactory): Int
}

interface ViewTypeFactory {
    fun viewType(style: Empty): Int
    fun viewType(style: SimpleText): Int
    fun viewType(style: InputText): Int
    fun viewType(style: Checkbox): Int
    fun viewType(style: Toggle): Int
    fun viewType(style: CustomPicker): Int
    fun viewType(style: DatePicker): Int
    fun viewType(style: Picker): Int
    fun viewType(style: ExceptionText): Int
    fun viewType(style: Loading): Int
    fun viewType(style: Action): Int
}

interface ViewHolderFactory {
    fun createViewHolder(viewType: Int, v: View): RecyclerView.ViewHolder
}

interface CanBeDisabled {
    fun alpha(disabled: Boolean) = if (disabled) 0.4f else 1f
}

interface StorageProvider {
    fun getStorage(): FormStorage
}

interface FormView : it.facile.form.ui.View {
    fun init(pageViewModels: List<PageViewModel>)
    fun updateField(path: FieldPath,
                    pageViewModel: PageViewModel)

    fun observeValueChanges(): Observable<FieldPathWithValue>
    fun showErrors(show: Boolean)
}


/** Convenient abstract class for createing a view that contains a single page of a form. s*/
interface PageFormView : FormView {
    var sectionsAdapter: SectionsAdapter?

    override fun init(pageViewModels: List<PageViewModel>) {
        if (sectionsAdapter == null) {
            sectionsAdapter = SectionsAdapter(
                    sectionViewModels = pageViewModels[0].sections,
                    fieldsLayouts = getFieldsLayouts(),
                    customActions = getCustomActions(),
                    customPickerActions = getCustomPickerActions())
            getRecyclerView().adapter = sectionsAdapter
        }
    }

    override fun updateField(path: FieldPath, pageViewModel: PageViewModel) {
        sectionsAdapter?.updateField(path, pageViewModel.sections[path.sectionIndex])
    }

    override fun observeValueChanges(): Observable<FieldPathWithValue> {
        return sectionsAdapter?.observeValueChanges()?.map { FieldPath(it.first.fieldIndex, it.first.sectionIndex, 0) pathTo it.second } ?: Observable.empty()
    }

    override fun showErrors(show: Boolean) {
        sectionsAdapter?.showErrors(show)
    }

    fun getRecyclerView(): RecyclerView

    fun getFieldsLayouts(): FieldsLayouts = FieldsLayouts()

    fun getCustomPickerActions(): Map<CustomPickerId, ((FieldValue) -> Unit) -> Unit> = emptyMap()

    fun getCustomActions(): Map<String, () -> Unit> = emptyMap()
}