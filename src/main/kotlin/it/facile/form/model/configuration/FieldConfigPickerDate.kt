package it.facile.form.model.configuration

import it.facile.form.Dates
import it.facile.form.format
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldValue.DateValue
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle
import java.text.DateFormat
import java.util.*

class FieldConfigPickerDate(label: String,
                            val minDate: Date = Dates.create(1900, 0, 1),
                            val maxDate: Date = Dates.create(2100, 11, 31),
                            val dateFormatter: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM),
                            val placeholder: String = "Select a date",
                            val rules: List<FieldRule> = emptyList()) : FieldConfig(label), FieldRulesValidator {

    override fun getViewModel(value: FieldValue, hidden: Boolean): FieldViewModel {
        return FieldViewModel(label, getViewModelStyle(value), hidden, isValid(rules, value))
    }

    override fun getViewModelStyle(value: FieldValue): FieldViewModelStyle =
            when (value) {
                is DateValue, Missing -> {
                    val selectedDate = (value as? DateValue)?.date
                    FieldViewModelStyle.DatePicker(
                            minDate,
                            maxDate,
                            selectedDate = selectedDate ?: Dates.today(),
                            dateText = selectedDate?.format(dateFormatter) ?: placeholder)
                }
                else -> FieldViewModelStyle.InvalidType()
            }
}