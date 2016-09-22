package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.R
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.viewmodel.FieldViewModel
import kotlinx.android.synthetic.main.form_field_loading.view.*

class FieldViewHolderLoading(itemView: View) : FieldViewHolderBase(itemView), CanBeHidden {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        itemView.loadingLabel.text = viewModel.label
    }

    override fun getHeight(): Int {
        return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
    }
}