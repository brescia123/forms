package it.facile.form.ui.adapters

import android.app.DatePickerDialog
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.facile.form.*
import it.facile.form.model.configuration.CustomPickerId
import it.facile.form.ui.adapters.ViewModelHolder
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.FieldViewModelStyle.*
import kotlinx.android.synthetic.main.form_field_checkbox.view.*
import kotlinx.android.synthetic.main.form_field_input_text.view.*
import kotlinx.android.synthetic.main.form_field_invalid_type.view.*
import kotlinx.android.synthetic.main.form_field_loading.view.*
import kotlinx.android.synthetic.main.form_field_text.view.*
import kotlinx.android.synthetic.main.form_field_toggle.view.*
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject

class FieldsAdapter(val viewModels: MutableList<FieldViewModel>,
                    val onCustomPickerClicked: (CustomPickerId, (FieldValue) -> Unit) -> Unit)
: RecyclerView.Adapter<FieldsAdapter.FieldViewHolder>() {

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

    val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>> = PublishSubject.create()

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
        is CustomPicker -> SIMPLE_TEXT_VIEW
        is DatePicker -> SIMPLE_TEXT_VIEW
        is Picker -> SIMPLE_TEXT_VIEW
        is InvalidType -> INVALID_TYPE_VIEW
        is Loading -> LOADING_VIEW
    }

    override fun getItemCount(): Int = viewModels.size

    fun getViewModel(position: Int): FieldViewModel = viewModels[position]

    fun setFieldViewModel(position: Int, fieldViewModel: FieldViewModel): FieldViewModel = viewModels.set(position, fieldViewModel)


    /* ========== VIEWHOLDERS ========== */

    abstract inner class FieldViewHolder(view: View) : ViewModelHolder(view) {
        open fun bind(viewModel: FieldViewModel, position: Int) {
            hide(viewModel)
        }
    }


    /* ---------- EMPTY ---------- */

    inner class ViewHolderEmpty(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
        }
    }


    /* ---------- TEXT ---------- */

    inner class ViewHolderText(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            itemView.textLabel.text = viewModel.label
            itemView.setOnClickListener(null)
            val style = viewModel.style
            when (style) {
                is SimpleText -> itemView.textValue.text = style.text
                is CustomPicker -> {
                    itemView.textValue.text = style.valueText
                    itemView.setOnClickListener {
                        onCustomPickerClicked(
                                style.identifier,
                                { notifyNewValue(position, it) })
                    }
                }
                is DatePicker -> {
                    val date = style.selectedDate
                    itemView.textValue.text = style.dateText
                    itemView.setOnClickListener {
                        val datePickerDialog = DatePickerDialog(
                                itemView.context,
                                { datePicker, year, month, day ->
                                    notifyNewValue(position, FieldValue.DateValue(Dates.create(year, month, day)))
                                },
                                date.year(),
                                date.month(),
                                date.dayOfMonth())
                        datePickerDialog.datePicker.minDate = style.minDate.time
                        datePickerDialog.datePicker.maxDate = style.maxDate.time
                        datePickerDialog.show()
                    }
                }
                is Picker -> {
                    itemView.textValue.text = style.valueText
                    itemView.setOnClickListener {
                        AlertDialog.Builder(itemView.context).setItems(
                                style.possibleValues.map { it.describe() }.toTypedArray(),
                                { dialogInterface, i ->
                                    notifyNewValue(position, FieldValue.Object(style.possibleValues[i]))
                                })
                                .setTitle(viewModel.label)
                                .create().show()
                    }
                }
            }
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_big).toInt()
        }
    }


    /* ---------- INPUT TEXT ---------- */

    inner class ViewHolderInputText(itemView: View) : FieldViewHolder(itemView) {
        private var subscription: Subscription? = null

        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            itemView.inputValue.hint = viewModel.label
            val editText = itemView.inputValue.editText
            val style = viewModel.style
            when (style) {
                is InputText -> {
                    editText?.setText(style.text)

                    // Listen for new values:

                    // If ENTER on keyboard tapped notify new value
                    editText?.setOnKeyListener { view, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                            notifyNewValue(position, FieldValue.Text(editText.text.toString()))
                        }
                        false
                    }
                    // If focus is lost notify new value
                    editText?.setOnFocusChangeListener { view, b -> if (!b) notifyNewValue(position, FieldValue.Text(editText.text.toString())) }
                    // If new char typed notify new value (unsubscribe from previous subscription)
                    subscription?.unsubscribe()
                    subscription = RxTextChangedWrapper.wrap(editText)?.subscribe(
                            { charSequence -> notifyNewValue(position, FieldValue.Text(editText?.text.toString())) },
                            { throwable -> Log.e(TAG, throwable.message) })
                }
            }
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_big).toInt()
        }
    }


    /* ---------- CHECKBOX ---------- */

    inner class ViewHolderCheckBox(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            itemView.checkboxLabel.text = viewModel.label
            val style = viewModel.style
            when (style) {
                is Checkbox -> {
                    val checkBoxValue = itemView.checkBoxValue
                    itemView.checkboxSecondLabel.text = style.boolText
                    checkBoxValue.setOnCheckedChangeListener(null)
                    checkBoxValue.isChecked = style.bool
                    checkBoxValue.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValue.Bool(value)) }
                    itemView.setOnClickListener { view -> checkBoxValue.isChecked = !checkBoxValue.isChecked }
                }
            }
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
        }
    }


    /* ---------- TOGGLE ---------- */

    inner class ViewHolderToggle(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            itemView.toggleLabel.text = viewModel.label
            val style = viewModel.style
            when (style) {
                is Toggle -> {
                    val toggleValue = itemView.toggleValue
                    itemView.toggleSecondLabel.text = style.boolText
                    toggleValue.setOnCheckedChangeListener(null)
                    toggleValue.isChecked = style.bool
                    toggleValue.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValue.Bool(value)) }
                    itemView.setOnClickListener { view -> toggleValue.isChecked = !toggleValue.isChecked }
                }
            }
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
        }
    }


    /* ---------- LOADING ---------- */

    inner class ViewHolderLoading(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            itemView.loadingLabel.text = viewModel.label
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_medium).toInt()
        }
    }


    /* ---------- INVALID TYPE ---------- */

    inner class ViewHolderInvalidType(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            itemView.invalidTypelabel.text = viewModel.label
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_big).toInt()
        }
    }


    /* ---------- HELPER METHODS ---------- */

    private fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    fun observeValueChanges(): Observable<Pair<Int, FieldValue>> = valueChangesSubject.asObservable()

}
