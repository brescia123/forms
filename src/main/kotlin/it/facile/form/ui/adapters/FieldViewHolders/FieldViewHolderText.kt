package it.facile.form.ui.adapters.FieldViewHolders

import android.app.DatePickerDialog
import android.support.v7.app.AlertDialog
import android.text.method.LinkMovementMethod
import android.view.View
import it.facile.form.*
import it.facile.form.model.CustomPickerId
import it.facile.form.storage.DescribableWithKey
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.DateValue
import it.facile.form.storage.FieldValue.Object
import it.facile.form.ui.CanBeDisabled
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_text.view.*
import rx.subjects.PublishSubject
import java.util.*

class FieldViewHolderText(itemView: View,
                          private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>,
                          private val customPickerActions: Map<CustomPickerId, ((FieldValue) -> Unit) -> Unit>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError, CanBeDisabled {

    private fun datePickerClickListener(position: Int, date: Date, minDate: Date, maxDate: Date): (View) -> Unit = {
        val datePickerDialog = DatePickerDialog(
                itemView.context,
                { datePicker, year, month, day ->
                    notifyNewValue(position, DateValue(Dates.create(year, month, day)))
                },
                date.year(),
                date.month(),
                date.dayOfMonth())
        datePickerDialog.datePicker.minDate = minDate.time
        datePickerDialog.datePicker.maxDate = maxDate.time
        datePickerDialog.show()
    }

    private fun pickerClickListener(position: Int, possibleValues: List<DescribableWithKey>, label: String): (View) -> Unit = {
        AlertDialog.Builder(itemView.context).setItems(
                possibleValues.map { it.textDescription }.toTypedArray(),
                { dialogInterface, i ->
                    notifyNewValue(position, Object(possibleValues[i]))
                })
                .setTitle(label)
                .create().show()
    }

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        val disabled = viewModel.disabled

        itemView.textLabel.text = viewModel.label.toHtmlSpanned()
        if (viewModel.label.containsLink())
            itemView.checkboxLabel.movementMethod = LinkMovementMethod.getInstance()
        itemView.textView.text = viewModel.style.textDescription
        itemView.textErrorText.text = viewModel.error

        itemView.textView.alpha = alpha(disabled)
        itemView.textErrorText.alpha = alpha(disabled)
        itemView.textErrorImage.alpha = alpha(disabled)

        itemView.setOnClickListener(null) // Remove old listener
        itemView.isClickable = not(disabled)

        when (style) {
            is FieldViewModelStyle.CustomPicker -> {
                val customPickerListener: (View) -> Unit = {
                    customPickerActions[style.identifier]?.invoke { notifyNewValue(position, it) }
                }
                itemView.setOnClickListener(if (viewModel.disabled) null else customPickerListener)
                itemView.isClickable = not(disabled)
            }
            is FieldViewModelStyle.DatePicker -> {
                val date = style.selectedDate
                val minDate = style.minDate
                val maxDate = style.maxDate
                itemView.setOnClickListener(if (viewModel.disabled) null else datePickerClickListener(position, date, minDate, maxDate))
                itemView.isClickable = not(disabled)
            }
            is FieldViewModelStyle.Picker -> {
                itemView.setOnClickListener(if (viewModel.disabled) null else pickerClickListener(position, style.possibleValues, viewModel.label))
                itemView.isClickable = not(disabled)
            }
        }
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        if (show && viewModel.error != null) {
            itemView.textView.invisible()
            itemView.textErrorText.visible()
            itemView.textErrorImage.visible()
        } else {
            itemView.textView.visible()
            itemView.textErrorText.invisible()
            itemView.textErrorImage.gone()
        }
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
            itemView.textErrorText.text.toString() != viewModel.error

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }
}