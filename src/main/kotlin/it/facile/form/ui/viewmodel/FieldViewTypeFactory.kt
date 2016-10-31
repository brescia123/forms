package it.facile.form.ui.viewmodel

import it.facile.form.ui.ViewTypeFactory
import it.facile.form.ui.adapters.FieldsLayouts
import it.facile.form.ui.viewmodel.FieldViewModelStyle.*

class FieldViewTypeFactory(val fieldsLayouts: FieldsLayouts) : ViewTypeFactory {
    override fun viewType(style: Empty) = fieldsLayouts.empty
    override fun viewType(style: SimpleText) = fieldsLayouts.text
    override fun viewType(style: InputText) = fieldsLayouts.inputText
    override fun viewType(style: Checkbox) = fieldsLayouts.checkBox
    override fun viewType(style: Toggle) = fieldsLayouts.toggle
    override fun viewType(style: CustomPicker) = fieldsLayouts.text
    override fun viewType(style: DatePicker) = fieldsLayouts.text
    override fun viewType(style: Picker) = fieldsLayouts.text
    override fun viewType(style: ExceptionText) = fieldsLayouts.exceptionText
    override fun viewType(style: Loading) = fieldsLayouts.loading
    override fun viewType(style: Action) = fieldsLayouts.customBehaviours
}