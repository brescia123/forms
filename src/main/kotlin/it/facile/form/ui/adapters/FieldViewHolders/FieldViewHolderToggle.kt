package it.facile.form.ui.adapters.FieldViewHolders

import android.text.method.LinkMovementMethod
import android.view.View
import it.facile.form.*
import it.facile.form.storage.FieldValue
import it.facile.form.ui.CanBeDisabled
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_toggle.view.*
import rx.subjects.PublishSubject

class FieldViewHolderToggle(itemView: View,
                            private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError, CanBeDisabled {

    private val itemViewClickListener = { view: View -> view.toggleView.isChecked = !view.toggleView.isChecked }

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        val disabled = viewModel.disabled
        itemView.setOnClickListener(if (disabled) null else itemViewClickListener)
        itemView.isClickable = not(disabled)
        itemView.toggleLabel.text = viewModel.label.toHtmlSpanned()
        if (viewModel.label.containsLink())
            itemView.checkboxLabel.movementMethod = LinkMovementMethod.getInstance()
        itemView.toggleTextView.text = viewModel.style.textDescription
        itemView.toggleTextView.alpha = alpha(disabled)
        when (style) {
            is FieldViewModelStyle.Toggle -> {
                val toggleView = itemView.toggleView
                toggleView.setOnCheckedChangeListener(null)
                toggleView.isChecked = style.bool
                toggleView.isEnabled = not(disabled)
                toggleView.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValue.Bool(value)) }
            }
        }
    }

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        if (show && viewModel.error != null) {
            itemView.toggleErrorImage.visible()
        } else {
            itemView.toggleErrorImage.gone()
        }
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean = true
}