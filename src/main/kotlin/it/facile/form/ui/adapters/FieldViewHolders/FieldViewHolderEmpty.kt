package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.R
import it.facile.form.viewmodel.FieldViewModel

class FieldViewHolderEmpty(itemView: View) : FieldViewHolderBase(itemView), CanBeHidden {
    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
    }

    override fun getHeight(): Int {
        return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
    }
}