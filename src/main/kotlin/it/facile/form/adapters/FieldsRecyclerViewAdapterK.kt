package it.facile.form.adapters

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import it.facile.form.R
import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.FieldViewModelStyleK.*
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_input_text.view.*
import kotlinx.android.synthetic.main.form_field_invalid_type.view.*
import kotlinx.android.synthetic.main.form_field_loading.view.*
import kotlinx.android.synthetic.main.form_field_text.view.*
import kotlinx.android.synthetic.main.form_field_toggle.view.*
import rx.Subscription

class FieldsRecyclerViewAdapterK(val viewModels: MutableList<FieldViewModelK>,
                                 val onFieldChangedListener: (absolutePosition: Int, fieldValue: FieldValueK) -> Unit) : RecyclerView.Adapter<FieldsRecyclerViewAdapterK.FieldViewHolder>() {

    companion object {
        private val TAG = "FieldsRecyclerViewAdapter"

        private val EMPTY_VIEW = R.layout.form_field_empty
        private val SIMPLE_TEXT_VIEW = R.layout.form_field_text
        private val INPUT_TEXT_VIEW = R.layout.form_field_input_text
        private val CHECKBOX_VIEW = R.layout.form_field_checkbox
        private val TOGGLE_VIEW = R.layout.form_field_toggle
        private val INVALID_TYPE_VIEW = R.layout.form_field_invalid_type
        private val LOADING_VIEW = R.layout.form_field_loading
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            EMPTY_VIEW -> ViewHolderEmpty(v)
            SIMPLE_TEXT_VIEW -> ViewHolderText(v)
            INPUT_TEXT_VIEW -> ViewHolderInputText(v)
            CHECKBOX_VIEW -> ViewHolderCheckBox(v)
            TOGGLE_VIEW -> ViewHolderToggle(v)
            INVALID_TYPE_VIEW -> ViewHolderInvalidType(v)
            LOADING_VIEW -> ViewHolderLoading(v)
            else -> ViewHolderEmpty(v)
        }
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) = holder.bind(viewModels[position], position)

    override fun getItemViewType(position: Int): Int = when (viewModels[position].style) {
        is Empty -> EMPTY_VIEW
        is SimpleText -> SIMPLE_TEXT_VIEW
        is InputText -> INPUT_TEXT_VIEW
        is Checkbox -> CHECKBOX_VIEW
        is Toggle -> TOGGLE_VIEW
        is DatePicker -> SIMPLE_TEXT_VIEW
        is Picker -> SIMPLE_TEXT_VIEW
        is InvalidType -> INVALID_TYPE_VIEW
        is Loading -> LOADING_VIEW
    }

    override fun getItemCount(): Int = viewModels.size

    fun getViewModel(position: Int): FieldViewModelK = viewModels[position]

    fun setFieldViewModel(position: Int, fieldViewModel: FieldViewModelK) {
        viewModels[position] = fieldViewModel
    }

    abstract inner class FieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(viewModel: FieldViewModelK, position: Int)
    }


    /* ---------- EMPTY ---------- */

    inner class ViewHolderEmpty(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModelK, position: Int) {
        }
    }


    /* ---------- TEXT ---------- */

    inner class ViewHolderText(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModelK, position: Int) {
            itemView.textLabel.text = viewModel.label
            val style = viewModel.style
            when (style) {
                is SimpleText -> itemView.textValue.text = style.text
                is DatePicker -> {
                    itemView.textValue.text = style.selectedDate.toString()
                    itemView.setOnClickListener {
                        Toast.makeText(itemView.context, "TODO", Toast.LENGTH_SHORT).show()
                    }
                }
                is Picker -> {
                    itemView.textValue.text = style.valueText
                    itemView.setOnClickListener {
                        AlertDialog.Builder(itemView.context).setItems(
                                style.possibleValues.map { it.describe() }.toTypedArray(),
                                { dialogInterface, i ->
                                    notifyNewValue(position, FieldValueK.Object(style.possibleValues[i]))
                                })
                                .setTitle(viewModel.label)
                                .create().show()
                    }
                }
            }
        }
    }


    /* ---------- INPUT TEXT ---------- */

    inner class ViewHolderInputText(itemView: View) : FieldViewHolder(itemView) {
        private var subscription: Subscription? = null

        override fun bind(viewModel: FieldViewModelK, position: Int) {
            itemView.inputValue.hint = viewModel.label
            val editText = itemView.inputValue.editText
            val style = viewModel.style
            when (style) {
                is InputText -> {
                    editText?.setText(style.text)

                    // Listen for new values:

                    // If ENTER on keyboard tapped notify new value
                    editText?.setOnKeyListener { view, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            notifyNewValue(position, FieldValueK.Text(editText.text.toString()))
                        }
                        false
                    }
                    // If focus is lost notify new value
                    editText?.setOnFocusChangeListener { view, b -> if (!b) notifyNewValue(position, FieldValueK.Text(editText.text.toString())) }
                    // If new char typed notify new value (unsubscribe from previous subscription)
                    subscription?.unsubscribe()
                    subscription = it.facile.form.RxTextChangedWrapper.wrap(editText)?.subscribe(
                            { charSequence -> notifyNewValue(position, FieldValueK.Text(editText?.text.toString())) },
                            { throwable -> Log.e(TAG, throwable.message) })
                }
            }
        }
    }


    /* ---------- CHECKBOX ---------- */

    inner class ViewHolderCheckBox(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModelK, position: Int) {
            itemView.checkboxLabel.text = viewModel.label
            val style = viewModel.style
            when (style) {
                is Checkbox -> {
                    val checkBoxValue = itemView.checkBoxValue
                    checkBoxValue.setOnCheckedChangeListener(null)
                    checkBoxValue.isChecked = style.bool
                    checkBoxValue.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValueK.Bool(value)) }
                    itemView.setOnClickListener { view -> checkBoxValue.isChecked = !checkBoxValue.isChecked }
                }
            }
        }
    }


    /* ---------- TOGGLE ---------- */

    inner class ViewHolderToggle(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModelK, position: Int) {
            itemView.toggleLabel.text = viewModel.label
            val style = viewModel.style
            when (style) {
                is Toggle -> {
                    val toggleValue = itemView.toggleValue
                    toggleValue.setOnCheckedChangeListener(null)
                    toggleValue.isChecked = style.bool
                    toggleValue.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValueK.Bool(value)) }
                    itemView.setOnClickListener { view -> toggleValue.isChecked = !toggleValue.isChecked }
                }
            }
        }
    }


    /* ---------- LOADING ---------- */


    inner class ViewHolderLoading(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModelK, position: Int) {
            itemView.loadingLabel.text = viewModel.label
        }
    }


    /* ---------- INVALID TYPE ---------- */

    inner class ViewHolderInvalidType(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModelK, position: Int) {
            itemView.invalidTypelabel.text = viewModel.label
        }
    }


    /* ---------- HELPER METHODS ---------- */

    private fun notifyNewValue(position: Int, newValue: FieldValueK) {
        onFieldChangedListener(position, newValue)
    }
}
