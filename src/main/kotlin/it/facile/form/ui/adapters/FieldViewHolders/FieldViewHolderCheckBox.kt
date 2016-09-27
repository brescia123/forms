package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.R
import it.facile.form.gone
import it.facile.form.show
import it.facile.form.storage.FieldValue
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_toggle.view.*
import rx.subjects.PublishSubject

class FieldViewHolderCheckBox(itemView: View,
                              private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        itemView.checkboxLabel.text = viewModel.label
        itemView.checkboxTextView.text = viewModel.style.textDescription
        when (style) {
            is FieldViewModelStyle.Checkbox -> {
                val checkBoxValue = itemView.checkBoxValue
                checkBoxValue.setOnCheckedChangeListener(null)
                checkBoxValue.isChecked = style.bool
                checkBoxValue.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValue.Bool(value)) }
                itemView.setOnClickListener { view -> checkBoxValue.isChecked = !checkBoxValue.isChecked }
            }
        }
    }

    override fun getHeight(): Int {
        return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
    }

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        if (show && viewModel.error != null) {
            itemView.checkboxImageError.show()
        } else {
            itemView.checkboxImageError.gone()
        }
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean = true
}