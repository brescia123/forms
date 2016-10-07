package it.facile.form.ui.adapters.FieldViewHolders

import android.support.design.widget.TextInputLayout
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import it.facile.form.*
import it.facile.form.storage.FieldValue
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle
import kotlinx.android.synthetic.main.form_field_input_text.view.*
import rx.Subscription
import rx.subjects.PublishSubject

class FieldViewHolderInputText(itemView: View,
                               private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError {

    var subscription: Subscription? = null

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        setLabel(viewModel.label)
        if (isTextChanged(viewModel, editText)) editText.setText(style.textDescription)
        editText.setOnFocusChangeListener(null)
        editText.setOnKeyListener(null)
        subscription?.unsubscribe()
        when (style) {
            is FieldViewModelStyle.InputText -> {
                // Listen for new values:

                // If ENTER on keyboard tapped notify new value
                editText.setOnKeyListener { view, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                        logD("Pressed Enter button")
                        editText.clearFocus()
                    }
                    false
                }

                editText.setOnEditorActionListener { view, i, keyEvent ->
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        logD("Pressed Done button")
                        editText.clearFocus()
                    }
                    false
                }

                // If focus is lost notify new value
                editText.setOnFocusChangeListener { view, b ->
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
                            val text = editText.text.toString()
                            logD("Notify position=$p, val=$text cause new char entered")
                            notifyNewValue(p, FieldValue.Text(text))
                        },
                        { throwable -> logE(throwable.message) })
            }
        }
    }

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        setErrorText(if (show) viewModel.error else null)
        if (hasErrorImageView) showErrorImage(show)
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
            if (hasErrorTextView) viewModel.error != errorTextView.text.toString()
            else viewModel.error != itemView.inputValue.error.toString()


    private fun isTextChanged(viewModel: FieldViewModel, editText: EditText?) =
            viewModel.style.textDescription != editText?.text.toString()

    private val hasInputValue by lazy { itemView.findViewById(R.id.inputValue) != null }


    private val hasErrorTextView by lazy { itemView.findViewById(R.id.inputErrorText) != null }
    private val hasErrorImageView by lazy { itemView.findViewById(R.id.inputErrorImage) != null }
    private val inputValue by lazy { itemView.findViewById(R.id.inputValue) as TextInputLayout }
    private val editText by lazy { itemView.findViewById(R.id.inputEditText) as EditText }
    private val labelTextView by lazy { itemView.findViewById(R.id.inputLabel) as TextView }
    private val errorTextView by lazy { itemView.findViewById(R.id.inputErrorText) as TextView }
    private val errorImageView by lazy { itemView.findViewById(R.id.inputErrorImage) as ImageView }

    private fun setLabel(label: String) {
        if (hasInputValue) inputValue.hint = label
        else labelTextView.text = label
    }

    private fun setErrorText(error: String?) {
        if (hasInputValue) inputValue.error = error
        else errorTextView.text = error
    }

    private fun showErrorImage(show: Boolean) {
        if (show) errorImageView.visible(true)
        else errorImageView.invisible(true)
    }
}
