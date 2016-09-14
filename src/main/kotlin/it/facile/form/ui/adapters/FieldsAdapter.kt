package it.facile.form.ui.adapters

import android.app.DatePickerDialog
import android.content.Context
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import it.facile.form.*
import it.facile.form.model.configuration.CustomPickerId
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
    var errorsShouldBeVisible = false

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
            hide(viewModel.isHidden())
            if (this is FieldViewHolderWithError) {
                showError(itemView, viewModel, errorsShouldBeVisible)
                //if (isErrorOutdated(itemView, viewModel) and errorsShouldBeVisible) animateError(itemView)
            }

        }
    }

    interface FieldViewHolderWithError {

        fun animateError(itemView: View) {
            val loadAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.vibrate)
            itemView.animation = loadAnimation
            loadAnimation.start()
        }

        fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean)

        fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean
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

    inner class ViewHolderText(itemView: View) : FieldViewHolder(itemView), FieldViewHolderWithError {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            val style = viewModel.style
            itemView.textLabel.text = viewModel.label
            itemView.textValue.text = viewModel.style.textDescription
            itemView.textError.text = viewModel.error
            itemView.setOnClickListener(null) // Remove old listener
            when (style) {
                is CustomPicker -> {
                    itemView.setOnClickListener {
                        onCustomPickerClicked(
                                style.identifier,
                                { notifyNewValue(position, it) })
                    }
                }
                is DatePicker -> {
                    val date = style.selectedDate
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

        override fun getHeight(): Int = itemView.resources.getDimension(R.dimen.field_height_big).toInt()

        override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
            if (show && viewModel.error != null) {
                itemView.textValue.hide()
                itemView.textError.show()
            } else {
                itemView.textValue.show()
                itemView.textError.hide()
            }
        }

        override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
                itemView.textError.text.toString() != viewModel.error
    }


    /* ---------- INPUT TEXT ---------- */

    inner class ViewHolderInputText(itemView: View) : FieldViewHolder(itemView), FieldViewHolderWithError {
        var subscription: Subscription? = null

        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            val style = viewModel.style
            val editText = itemView.inputValue.editText
            itemView.inputValue.hint = viewModel.label
            if (isTextChanged(viewModel, editText)) editText?.setText(style.textDescription)
            editText?.setOnFocusChangeListener(null)
            editText?.setOnKeyListener(null)
            subscription?.unsubscribe()
            when (style) {
                is InputText -> {
                    // Listen for new values:

                    // If ENTER on keyboard tapped notify new value
                    editText?.setOnKeyListener { view, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                            logD("Pressed Enter button")
                            editText.clearFocus()
                        }
                        false
                    }

                    editText?.setOnEditorActionListener { view, i, keyEvent ->
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            logD("Pressed Done button")
                            editText.clearFocus()
                        }
                        false
                    }

                    // If focus is lost notify new value
                    editText?.setOnFocusChangeListener { view, b ->
                        if (!b) {
                            val text = editText.text.toString()
                            logD("Notify position=$position, val=$text cause focus lost")
                            notifyNewValue(position, FieldValue.Text(text))
                        }
                    }

                    // If new char typed notify new value
                    subscription = RxTextChangedWrapper.wrap(editText, false)?.subscribe(
                            { charSequence ->
                                val p = position
                                val text = editText?.text.toString()
                                logD("Notify position=$p, val=$text cause new char entered")
                                notifyNewValue(p, FieldValue.Text(text))
                            },
                            { throwable -> Log.e(TAG, throwable.message) })
                }
            }
        }

        override fun getHeight(): Int {
            return itemView.resources.getDimension(R.dimen.field_height_input_text).toInt()
        }

        override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
            itemView.inputValue.error = if (show) viewModel.error else null
        }

        override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
                viewModel.error != itemView.inputValue.error.toString()
    }


    /* ---------- CHECKBOX ---------- */

    inner class ViewHolderCheckBox(itemView: View) : FieldViewHolder(itemView) {
        override fun bind(viewModel: FieldViewModel, position: Int) {
            super.bind(viewModel, position)
            val style = viewModel.style
            itemView.checkboxLabel.text = viewModel.label
            itemView.checkboxSecondLabel.text = style.textDescription
            when (style) {
                is Checkbox -> {
                    val checkBoxValue = itemView.checkBoxValue
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
            val style = viewModel.style
            itemView.toggleLabel.text = viewModel.label
            itemView.toggleSecondLabel.text = style.textDescription
            when (style) {
                is Toggle -> {
                    val toggleView = itemView.toggleView
                    toggleView.setOnCheckedChangeListener(null)
                    toggleView.isChecked = style.bool
                    toggleView.setOnCheckedChangeListener { b, value -> notifyNewValue(position, FieldValue.Bool(value)) }
                    itemView.setOnClickListener { view -> toggleView.isChecked = !toggleView.isChecked }
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

    fun getThemeAccentColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }

    fun observeValueChanges(): Observable<Pair<Int, FieldValue>> = valueChangesSubject.asObservable()

    fun toggleErrorsVisibility() {
        errorsShouldBeVisible = errorsShouldBeVisible.not()
    }

    /** Return the position of the first error, -1 if no error are present */
    fun firstErrorPosition(): Int {
        for ((index, viewModel) in viewModels.withIndex()) {
            if (viewModel.error != null) {
                return index
            }
        }
        return -1
    }

    fun errorPositions(): MutableList<Int> {
        val positions = mutableListOf<Int>()
        for ((index, viewModel) in viewModels.withIndex()) {
            if (viewModel.error != null) {
                positions.add(index)
            }
        }
        return positions
    }

    fun hasErrors() = firstErrorPosition() >= 0

    fun TextView.setTextAppearanceCompat(resId: Int) {
        TextViewCompat.setTextAppearance(this, resId)
    }

    fun isTextChanged(viewModel: FieldViewModel, editText: EditText?) =
            !viewModel.style.textDescription.equals(editText?.text.toString())
}
