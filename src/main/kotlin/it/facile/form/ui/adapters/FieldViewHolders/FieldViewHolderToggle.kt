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
import kotlinx.android.synthetic.main.form_field_toggle.view.*
import rx.subjects.PublishSubject

class FieldViewHolderToggle(itemView: View,
                            private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        itemView.toggleLabel.text = viewModel.label
        when (style) {
            is FieldViewModelStyle.Toggle -> {
                val toggleView = itemView.toggleView
                toggleView.setOnCheckedChangeListener(null)
                toggleView.isChecked = style.bool
                toggleView.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValue.Bool(value)) }
                itemView.setOnClickListener { view -> toggleView.isChecked = !toggleView.isChecked }
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
            itemView.toggleImageError.show()
        } else {
            itemView.toggleImageError.gone()
        }
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean = true
}