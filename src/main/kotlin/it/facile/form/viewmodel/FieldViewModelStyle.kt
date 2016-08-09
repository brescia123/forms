package it.facile.form.viewmodel

import it.facile.form.model.configuration.CustomPickerId
import java.util.*

sealed class FieldViewModelStyle {
    object Empty : FieldViewModelStyle()
    class InvalidType : FieldViewModelStyle()
    class SimpleText(val text: String) : FieldViewModelStyle()
    class InputText(val text: String) : FieldViewModelStyle()
    class Checkbox(val bool: Boolean, val boolText: String) : FieldViewModelStyle()
    class Toggle(val bool: Boolean, val boolText: String) : FieldViewModelStyle()
    class DatePicker(val minDate: Date, val maxDate: Date, val selectedDate: Date, val dateText: String) : FieldViewModelStyle()
    class CustomPicker(val identifier: CustomPickerId, val valueText: String) : FieldViewModelStyle()
    class Picker(val possibleValues: List<DescribableWithKey>, val valueText: String) : FieldViewModelStyle()
    class Loading() : FieldViewModelStyle()

    override fun toString(): String = when (this) {
        is Empty -> "Empty"
        is InvalidType -> "Invalid type"
        is SimpleText -> text
        is InputText -> text
        is Checkbox -> bool.toString()
        is Toggle -> bool.toString()
        is DatePicker -> dateText
        is CustomPicker -> valueText
        is Picker -> valueText
        is Loading -> "Loading"
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else when (this) {
            is Empty -> true
            is InvalidType -> true
            is SimpleText -> other is SimpleText && other.text.equals(text)
            is InputText -> other is InputText && other.text.equals(text)
            is Checkbox -> other is Checkbox && other.bool == bool
            is Toggle -> other is Toggle && other.bool == bool
            is CustomPicker -> other is CustomPicker
                    && other.identifier.equals(identifier)
                    && other.valueText.equals(valueText)
            is DatePicker -> other is DatePicker
                    && other.selectedDate.equals(selectedDate)
                    && other.maxDate.equals(maxDate)
                    && other.minDate.equals(minDate)
                    && other.dateText.equals(dateText)
            is Picker -> other is Picker
                    && other.possibleValues.equals(possibleValues)
                    && other.valueText.equals(valueText)
            is Loading -> true
        }
    }
}
