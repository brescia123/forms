package it.facile.form.ui.adapters

import it.facile.form.R

data class FieldsLayouts(val empty: Int = R.layout.form_field_empty,
                         val text: Int = R.layout.form_field_text,
                         val inputText: Int = R.layout.form_field_input_text,
                         val checkBox: Int = R.layout.form_field_checkbox,
                         val toggle: Int = R.layout.form_field_toggle,
                         val exceptionText: Int = R.layout.form_field_exception_text,
                         val loading: Int = R.layout.form_field_loading,
                         val action: Int = R.layout.form_field_custom_action,
                         val sectionHeaders: Pair<Int, Int?> = R.layout.form_section_header to R.layout.form_section_first_header)
