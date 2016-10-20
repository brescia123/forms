package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import android.widget.CompoundButton
import it.facile.form.gone
import it.facile.form.ui.CanBeDisabled
import it.facile.form.not
import it.facile.form.storage.FieldValue
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import it.facile.form.visible
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import rx.subjects.PublishSubject

class FieldViewHolderCheckBox(itemView: View,
                              private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError, CanBeDisabled {

    private val itemViewClickListener = { view: View -> view.checkboxView.isChecked = !view.checkboxView.isChecked }

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        val disabled = viewModel.disabled
        itemView.setOnClickListener(if (disabled) null else itemViewClickListener)
        itemView.isClickable = not(disabled)
        itemView.checkboxLabel.text = viewModel.label
        itemView.checkboxTextView.text = viewModel.style.textDescription
        itemView.checkboxTextView.alpha = alpha(disabled)
        when (style) {
            is FieldViewModelStyle.Checkbox -> {
                val checkBoxValue = itemView.checkboxView
                checkBoxValue.setOnCheckedChangeListener(null)
                checkBoxValue.isChecked = style.bool
                checkBoxValue.isEnabled = not(disabled)
                checkBoxValue.setOnCheckedChangeListener { b: CompoundButton, value: Boolean -> notifyNewValue(position, FieldValue.Bool(value)) }
            }
        }
    }

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        if (show && viewModel.error != null) {
            itemView.checkboxErrorImage.visible()
        } else {
            itemView.checkboxErrorImage.gone()
        }
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean = true
}