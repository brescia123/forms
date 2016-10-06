package it.facile.form.ui.adapters.FieldViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanShowError
import it.facile.form.ui.WithOriginalHeight
import it.facile.form.ui.viewmodel.FieldViewModel

abstract class FieldViewHolderBase(view: View) : RecyclerView.ViewHolder(view), WithOriginalHeight {
    override var originalHeight: Int = itemView.layoutParams.height
    open fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        if (this is CanBeHidden) hide(itemView, originalHeight, viewModel.isHidden())
        if (this is CanShowError) {
            showError(itemView, viewModel, errorsShouldBeVisible)
            //if (isErrorOutdated(itemView, viewModel) and errorsShouldBeVisible) animateError(itemView)
        }

    }
}
