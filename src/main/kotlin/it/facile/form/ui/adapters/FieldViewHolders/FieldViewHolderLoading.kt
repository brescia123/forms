package it.facile.form.ui.adapters.FieldViewHolders

import android.text.method.LinkMovementMethod
import android.view.View
import it.facile.form.containsLink
import it.facile.form.toHtmlSpanned
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.viewmodel.FieldViewModel
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_loading.view.*

class FieldViewHolderLoading(itemView: View) : FieldViewHolderBase(itemView), CanBeHidden {
    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        itemView.loadingLabel.text = viewModel.label.toHtmlSpanned()
        if (viewModel.label.containsLink())
            itemView.checkboxLabel.movementMethod = LinkMovementMethod.getInstance()
    }
}