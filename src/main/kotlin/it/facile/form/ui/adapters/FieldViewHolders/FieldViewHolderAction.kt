package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.Action
import kotlinx.android.synthetic.main.form_field_custom_action.view.*

class FieldViewHolderAction(v: View) : FieldViewHolderBase(v) {
    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        itemView.actionLabel.text = viewModel.label
        val style = viewModel.style
        if (style is Action)
            itemView.setOnClickListener{ style.action.invoke() }
    }
}
