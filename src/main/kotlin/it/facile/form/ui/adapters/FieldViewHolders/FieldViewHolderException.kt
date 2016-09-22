package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.R
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import kotlinx.android.synthetic.main.form_field_invalid_type.view.*

class FieldViewHolderException(itemView: View) : FieldViewHolderBase(itemView), CanBeHidden {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        itemView.exceptionLabel.text = viewModel.label
        val style = viewModel.style
        when (style) {
            is FieldViewModelStyle.ExceptionText -> itemView.exceptionValue.text = style.text
        }
    }

    override fun getHeight(): Int {
        return itemView.resources.getDimension(R.dimen.field_height_big).toInt()
    }
}