package it.facile.form.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import it.facile.form.storage.FieldValue
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.*

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