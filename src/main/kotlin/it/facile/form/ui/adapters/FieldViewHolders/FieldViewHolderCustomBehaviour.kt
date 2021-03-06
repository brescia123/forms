package it.facile.form.ui.adapters.FieldViewHolders

import android.text.method.LinkMovementMethod
import android.view.View
import it.facile.form.containsLink
import it.facile.form.not
import it.facile.form.toHtmlSpanned
import it.facile.form.ui.CanBeDisabled
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.Action
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_custom_behaviours.view.*

class FieldViewHolderCustomBehaviour(v: View,
                                     private val customBehaviours: Map<String, () -> Unit>) : FieldViewHolderBase(v), CanBeDisabled {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val disabled = viewModel.disabled
        itemView.actionLabel.text = viewModel.label.toHtmlSpanned()
        if (viewModel.label.containsLink())
            itemView.checkboxLabel.movementMethod = LinkMovementMethod.getInstance()
        itemView.actionImage.alpha = alpha(disabled)
        val style = viewModel.style
        if (style is Action) {
            val action: (View) -> Unit = { v: View ->
                customBehaviours[style.identifier]?.invoke()
            }
            itemView.setOnClickListener(if (disabled) null else action)
            itemView.isClickable = not(disabled)
        }
    }
}
