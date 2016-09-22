package it.facile.form.ui

import it.facile.form.R
import it.facile.form.ui.viewmodel.FieldViewModelStyle

class FieldViewTypeFactory : ViewTypeFactory {
    override fun viewType(style: FieldViewModelStyle.Empty) = R.layout.form_field_empty
    override fun viewType(style: FieldViewModelStyle.SimpleText) = R.layout.form_field_text
    override fun viewType(style: FieldViewModelStyle.InputText) = R.layout.form_field_input_text
    override fun viewType(style: FieldViewModelStyle.Checkbox) = R.layout.form_field_checkbox
    override fun viewType(style: FieldViewModelStyle.Toggle) = R.layout.form_field_toggle
    override fun viewType(style: FieldViewModelStyle.CustomPicker) = R.layout.form_field_text
    override fun viewType(style: FieldViewModelStyle.DatePicker) = R.layout.form_field_text
    override fun viewType(style: FieldViewModelStyle.Picker) = R.layout.form_field_text
    override fun viewType(style: FieldViewModelStyle.ExceptionText) = R.layout.form_field_exception_text
    override fun viewType(style: FieldViewModelStyle.Loading) = R.layout.form_field_loading
}