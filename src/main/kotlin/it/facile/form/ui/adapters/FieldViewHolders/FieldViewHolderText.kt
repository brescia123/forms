package it.facile.form.ui.adapters.FieldViewHolders

import android.app.DatePickerDialog
import android.support.v7.app.AlertDialog
import android.view.View
import it.facile.form.*
import it.facile.form.model.CustomPickerId
import it.facile.form.storage.FieldValue
import it.facile.form.storage.FieldValue.DateValue
import it.facile.form.storage.FieldValue.Object
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import kotlinx.android.synthetic.main.form_field_text.view.*
import rx.subjects.PublishSubject

class FieldViewHolderText(itemView: View,
                          private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>,
                          private val onCustomPickerClicked: (CustomPickerId, (FieldValue) -> Unit) -> Unit) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError {

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        itemView.textLabel.text = viewModel.label
        itemView.textValue.text = viewModel.style.textDescription
        itemView.textError.text = viewModel.error
        itemView.setOnClickListener(null) // Remove old listener
        when (style) {
            is FieldViewModelStyle.CustomPicker -> {
                itemView.setOnClickListener {
                    onCustomPickerClicked(
                            style.identifier,
                            { notifyNewValue(position, it) })
                }
            }
            is FieldViewModelStyle.DatePicker -> {
                val date = style.selectedDate
                itemView.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(
                            itemView.context,
                            { datePicker, year, month, day ->
                                notifyNewValue(position, DateValue(Dates.create(year, month, day)))
                            },
                            date.year(),
                            date.month(),
                            date.dayOfMonth())
                    datePickerDialog.datePicker.minDate = style.minDate.time
                    datePickerDialog.datePicker.maxDate = style.maxDate.time
                    datePickerDialog.show()
                }
            }
            is FieldViewModelStyle.Picker -> {
                itemView.setOnClickListener {
                    AlertDialog.Builder(itemView.context).setItems(
                            style.possibleValues.map { it.describe() }.toTypedArray(),
                            { dialogInterface, i ->
                                notifyNewValue(position, Object(style.possibleValues[i]))
                            })
                            .setTitle(viewModel.label)
                            .create().show()
                }
            }
        }
    }

    override fun getHeight(): Int = itemView.resources.getDimension(R.dimen.field_height_big).toInt()

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        if (show && viewModel.error != null) {
            itemView.textValue.hide()
            itemView.textError.show()
            itemView.imageError.show()
        } else {
            itemView.textValue.show()
            itemView.textError.hide()
            itemView.imageError.hide()
        }
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
            itemView.textError.text.toString() != viewModel.error

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }
}