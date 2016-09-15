package it.facile.form.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.facile.form.R
import it.facile.form.model.configuration.CustomPickerId
import it.facile.form.ui.adapters.FieldViewHolders.*
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle.*
import rx.Observable
import rx.subjects.PublishSubject

class FieldsAdapter(val viewModels: MutableList<FieldViewModel>,
                    val onCustomPickerClicked: (CustomPickerId, (FieldValue) -> Unit) -> Unit)
: RecyclerView.Adapter<FieldViewHolderBase>() {

    companion object {
        private val EMPTY_VIEW = R.layout.form_field_empty
        private val SIMPLE_TEXT_VIEW = R.layout.form_field_text
        private val INPUT_TEXT_VIEW = R.layout.form_field_input_text
        private val CHECKBOX_VIEW = R.layout.form_field_checkbox
        private val TOGGLE_VIEW = R.layout.form_field_toggle
        private val INVALID_TYPE_VIEW = R.layout.form_field_invalid_type
        private val LOADING_VIEW = R.layout.form_field_loading
    }

    val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>> = PublishSubject.create()
    var errorsShouldBeVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolderBase {
        val v = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            EMPTY_VIEW -> FieldViewHolderEmpty(v)
            SIMPLE_TEXT_VIEW -> FieldViewHolderText(v, valueChangesSubject, onCustomPickerClicked)
            INPUT_TEXT_VIEW -> FieldViewHolderInputText(v, valueChangesSubject)
            CHECKBOX_VIEW -> FieldViewHolderCheckBox(v, valueChangesSubject)
            TOGGLE_VIEW -> FieldViewHolderToggle(v, valueChangesSubject)
            INVALID_TYPE_VIEW -> FieldViewHolderInvalidType(v)
            LOADING_VIEW -> FieldViewHolderLoading(v)
            else -> FieldViewHolderEmpty(v)
        }
    }

    override fun onBindViewHolder(holder: FieldViewHolderBase, position: Int) = holder.bind(viewModels[position], position, errorsShouldBeVisible)

    override fun getItemViewType(position: Int): Int = when (viewModels[position].style) {
        is Empty -> EMPTY_VIEW
        is SimpleText -> SIMPLE_TEXT_VIEW
        is InputText -> INPUT_TEXT_VIEW
        is Checkbox -> CHECKBOX_VIEW
        is Toggle -> TOGGLE_VIEW
        is CustomPicker -> SIMPLE_TEXT_VIEW
        is DatePicker -> SIMPLE_TEXT_VIEW
        is Picker -> SIMPLE_TEXT_VIEW
        is InvalidType -> INVALID_TYPE_VIEW
        is Loading -> LOADING_VIEW
    }

    override fun getItemCount(): Int = viewModels.size

    fun getViewModel(position: Int): FieldViewModel = viewModels[position]

    fun setFieldViewModel(position: Int, fieldViewModel: FieldViewModel): FieldViewModel = viewModels.set(position, fieldViewModel)


    /* ---------- HELPER METHODS ---------- */

    fun observeValueChanges(): Observable<Pair<Int, FieldValue>> = valueChangesSubject.asObservable()

    fun toggleErrorsVisibility() {
        errorsShouldBeVisible = !errorsShouldBeVisible
    }

    /** Return the position of the first error, -1 if no error are present */
    fun firstErrorPosition(): Int {
        for ((index, viewModel) in viewModels.visibleFields().withIndex()) {
            if (viewModel.error != null) {
                return index
            }
        }
        return -1
    }

    fun errorPositions(): MutableList<Int> {
        val positions = mutableListOf<Int>()
        for ((index, viewModel) in viewModels.visibleFields().withIndex()) {
            if (viewModel.error != null) {
                positions.add(index)
            }
        }
        return positions
    }

    fun hasErrors() = firstErrorPosition() >= 0

    fun MutableList<FieldViewModel>.visibleFields(): List<FieldViewModel> {
        return filter { !it.hidden }
    }
}
