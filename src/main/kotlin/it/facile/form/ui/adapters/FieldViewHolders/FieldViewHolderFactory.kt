package it.facile.form.ui.adapters.FieldViewHolders

import android.view.View
import it.facile.form.storage.FieldValue
import it.facile.form.ui.ViewHolderFactory
import it.facile.form.ui.adapters.FieldsLayouts
import rx.subjects.PublishSubject

class FieldViewHolderFactory(val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>,
                             val customPickerActions: Map<String, ((FieldValue) -> Unit) -> Unit>,
                             val customBehaviours: Map<String, () -> Unit>,
                             val fieldsLayouts: FieldsLayouts) : ViewHolderFactory {
    override fun createViewHolder(viewType: Int,
                                  v: View): FieldViewHolderBase =
            when (viewType) {
                fieldsLayouts.empty -> FieldViewHolderEmpty(v)
                fieldsLayouts.text -> FieldViewHolderText(v, valueChangesSubject, customPickerActions)
                fieldsLayouts.inputText -> FieldViewHolderInputText(v, valueChangesSubject)
                fieldsLayouts.checkBox -> FieldViewHolderCheckBox(v, valueChangesSubject)
                fieldsLayouts.toggle -> FieldViewHolderToggle(v, valueChangesSubject)
                fieldsLayouts.exceptionText -> FieldViewHolderException(v)
                fieldsLayouts.loading -> FieldViewHolderLoading(v)
                fieldsLayouts.customBehaviours -> FieldViewHolderCustomBehaviour(v, customBehaviours)
                else -> FieldViewHolderEmpty(v)
            }
}