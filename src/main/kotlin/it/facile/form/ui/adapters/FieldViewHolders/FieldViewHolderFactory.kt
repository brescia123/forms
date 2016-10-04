package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.R
import it.facile.form.model.CustomPickerId
import it.facile.form.storage.FieldValue
import it.facile.form.ui.ViewHolderFactory
import it.facile.form.ui.adapters.FieldViewHolders.*
import rx.subjects.PublishSubject

class FieldViewHolderFactory() : ViewHolderFactory {
    override fun createViewHolder(viewType: Int,
                                  v: View,
                                  valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>,
                                  customPickerActions: Map<CustomPickerId, ((FieldValue) -> Unit) -> Unit>): FieldViewHolderBase =
            when (viewType) {
                R.layout.form_field_empty -> FieldViewHolderEmpty(v)
                R.layout.form_field_text -> FieldViewHolderText(v, valueChangesSubject, customPickerActions)
                R.layout.form_field_input_text -> FieldViewHolderInputText(v, valueChangesSubject)
                R.layout.form_field_checkbox -> FieldViewHolderCheckBox(v, valueChangesSubject)
                R.layout.form_field_toggle -> FieldViewHolderToggle(v, valueChangesSubject)
                R.layout.form_field_exception_text -> FieldViewHolderException(v)
                R.layout.form_field_loading -> FieldViewHolderLoading(v)
                else -> FieldViewHolderEmpty(v)
            }
}