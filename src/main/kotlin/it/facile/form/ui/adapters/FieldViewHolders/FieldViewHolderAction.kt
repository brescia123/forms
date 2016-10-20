package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.ui.CanBeDisabled
import it.facile.form.not
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.Action
import kotlinx.android.synthetic.main.form_field_custom_action.view.*

class FieldViewHolderAction(v: View) : FieldViewHolderBase(v), CanBeDisabled {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val disabled = viewModel.disabled
        itemView.actionLabel.text = viewModel.label
        itemView.actionImage.alpha = alpha(disabled)
        val style = viewModel.style
        if (style is Action) {
            val action = { v: View -> style.action.invoke() }
            itemView.setOnClickListener(if (disabled) null else action)
            itemView.isClickable = not(disabled)
        }
    }
}
