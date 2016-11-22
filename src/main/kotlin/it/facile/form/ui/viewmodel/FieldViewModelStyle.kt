package it.facile.form.ui.viewmodel

import it.facile.form.model.InputTextType
import it.facile.form.storage.DescribableWithKey
import it.facile.form.ui.ViewTypeFactory
import it.facile.form.ui.Visitable
import java.util.*

sealed class FieldViewModelStyle(val textDescription: String) : Visitable {

    object Empty : FieldViewModelStyle("Empty") {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class ExceptionText(val text: String) : FieldViewModelStyle(text) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class SimpleText(val text: String) : FieldViewModelStyle(text) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class InputText(val text: String, val inputTextType: InputTextType) : FieldViewModelStyle(text) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class Checkbox(val bool: Boolean, val boolText: String) : FieldViewModelStyle(boolText) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class Toggle(val bool: Boolean, val boolText: String) : FieldViewModelStyle(boolText) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class DatePicker(val minDate: Date, val maxDate: Date, val selectedDate: Date?, val dateText: String) : FieldViewModelStyle(dateText) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class CustomPicker(val identifier: String, val valueText: String) : FieldViewModelStyle(valueText) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class Picker(val possibleValues: List<DescribableWithKey>, val valueText: String) : FieldViewModelStyle(valueText) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class Action(val key: String, val identifier: String) : FieldViewModelStyle(key) {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    class Loading() : FieldViewModelStyle("Loading") {
        override fun viewType(viewTypeFactory: ViewTypeFactory) = viewTypeFactory.viewType(this)
    }

    override fun toString(): String = textDescription

    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else when (this) {
            is Empty -> other is Empty
            is ExceptionText -> other is ExceptionText
            is SimpleText -> other is SimpleText && other.text == text
            is InputText -> other is InputText && other.text == text
                    && other.inputTextType == inputTextType
            is Checkbox -> other is Checkbox && other.bool == bool
            is Toggle -> other is Toggle && other.bool == bool
            is CustomPicker -> other is CustomPicker
                    && other.identifier == identifier
                    && other.valueText == valueText
            is DatePicker -> other is DatePicker
                    && other.selectedDate == selectedDate
                    && other.maxDate == maxDate
                    && other.minDate == minDate
                    && other.dateText == dateText
            is Picker -> other is Picker
                    && other.possibleValues == possibleValues
                    && other.valueText == valueText
            is Loading -> other is Loading
            is Action -> other is Action
                    && other.key == key
                    && other.identifier == identifier
        }
    }

    override fun hashCode(): Int = when (this) {
        is Empty -> textDescription.hashCode()
        is ExceptionText -> textDescription.hashCode()
        is SimpleText -> textDescription.hashCode() * 31 + text.hashCode()
        is InputText -> textDescription.hashCode() * 31 + text.hashCode()
        is Checkbox -> (textDescription.hashCode() * 31 + bool.hashCode()) * 31 + boolText.hashCode()
        is Toggle -> (textDescription.hashCode() * 31 + bool.hashCode()) * 31 + boolText.hashCode()
        is DatePicker -> (((textDescription.hashCode() * 31 + minDate.hashCode()) * 31 + maxDate.hashCode()) * 31 + (selectedDate?.hashCode() ?: 0)) * 31 + dateText.hashCode()
        is CustomPicker -> (textDescription.hashCode() * 31 + identifier.hashCode()) * 31 + valueText.hashCode()
        is Picker -> (textDescription.hashCode() * 31 + possibleValues.hashCode()) * 31 + valueText.hashCode()
        is Loading -> textDescription.hashCode()
        is Action -> (textDescription.hashCode() * 31 + identifier.hashCode())
    }

    companion object {
        val INVALID_TYPE = "Invalid type"
    }
}

