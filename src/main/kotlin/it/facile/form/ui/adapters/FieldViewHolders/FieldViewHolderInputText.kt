package it.facile.form.ui.adapters.FieldViewHolders

import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.text.Spanned
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import it.facile.form.*
import it.facile.form.model.InputTextType
import it.facile.form.model.InputTextType.*
import it.facile.form.storage.FieldValue
import it.facile.form.ui.CanBeDisabled
import it.facile.form.ui.CanBeHidden
import it.facile.form.ui.CanNotifyNewValues
import it.facile.form.ui.CanShowError
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.FieldViewModelStyle.InputText
import kotlinx.android.synthetic.main.form_field_input_text.view.*
import rx.Subscription
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class FieldViewHolderInputText(itemView: View,
                               private val valueChangesSubject: PublishSubject<Pair<Int, FieldValue>>) :
        FieldViewHolderBase(itemView), CanBeHidden, CanNotifyNewValues, CanShowError, CanBeDisabled {

    private var subscription: Subscription? = null

    private val keyListener: (View, Int, KeyEvent?) -> Boolean = { view, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER && event?.action == KeyEvent.ACTION_UP) {
            logD("Pressed Enter button")
            editText?.clearFocus()
        }
        false
    }
    private val editorActionListener: (TextView, Int, KeyEvent?) -> Boolean = { view, i, keyEvent ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            logD("Pressed Done button")
            editText?.clearFocus()
        }
        false
    }

    private fun focusChangedListener(position: Int): (View, Boolean) -> Unit = { view, b ->
        if (!b) {
            val text = editText?.text.toString()
            logD("Notify position=$position, val=$text cause focus lost")
            notifyNewValue(position, FieldValue.Text(text))
        }
    }

    private fun rxEditText(editText: EditText?, inputTextType: InputTextType? = null) = editText?.wrap(false, inputTextType)
            ?.debounce(300, TimeUnit.MILLISECONDS)

    override fun bind(viewModel: FieldViewModel, position: Int, errorsShouldBeVisible: Boolean) {
        super.bind(viewModel, position, errorsShouldBeVisible)
        val style = viewModel.style
        val disabled = viewModel.disabled
        setLabel(viewModel.label.toHtmlSpanned())
        if (isTextChanged(viewModel, editText)) editText?.setText(style.textDescription)
        editText?.onFocusChangeListener = null
        editText?.setOnKeyListener(null)
        editText?.isEnabled = not(disabled)
        subscription?.unsubscribe()
        when (style) {
            is InputText -> {

                if (isInputTextTypeChanged(style.inputTextConfig.inputTextType, editText)) {
                    editText?.inputType = style.inputTextConfig.inputTextType.toAndroidInputType()

                    (style.inputTextConfig.inputTextType as? InputTextType.Multiline)?.let {
                        editText?.minLines = it.minLines
                        editText?.maxLines = it.maxLines
                    }
                }
                // Listen for new values:

                // If ENTER on keyboard tapped notify new value
                editText?.setOnKeyListener(if (disabled) null else (keyListener))

                editText?.setOnEditorActionListener(if (disabled) null else (editorActionListener))

                // If focus is lost notify new value
                editText?.setOnFocusChangeListener(if (disabled) null else (focusChangedListener(position)))

                // If new char typed notify new value
                if (not(disabled)) subscription = rxEditText(editText)?.subscribe(
                        { charSequence -> notifyNewValue(position, FieldValue.Text(editText?.text.toString())) },
                        { logE(it) })

            }
        }
    }

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        setErrorText(if (show) viewModel.error else null)
        if (hasErrorImageView) showErrorImage(show && viewModel.error != null)
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
            if (hasErrorTextView) viewModel.error != errorTextView.text.toString()
            else viewModel.error != itemView.inputValue.error.toString()


    private fun isTextChanged(viewModel: FieldViewModel, editText: EditText?) =
            viewModel.style.textDescription != editText?.text.toString()

    private fun isInputTextTypeChanged(type: InputTextType, editText: EditText?): Boolean = when (type) {
        CapWords, Email, Phone, Text -> editText?.inputType != type.toAndroidInputType()
        is InputTextType.Number -> editText?.inputType != type.toAndroidInputType()
        is InputTextType.Multiline -> {
            editText?.inputType != type.toAndroidInputType()
                    || editText.minLines == type.minLines
                    || editText.maxLines == type.maxLines
        }
    }


    private val hasInputValue by lazy { itemView.findViewById(R.id.inputValue) != null }
    private val hasErrorTextView by lazy { itemView.findViewById(R.id.inputErrorText) != null }
    private val hasErrorImageView by lazy { itemView.findViewById(R.id.inputErrorImage) != null }
    private val inputValue by lazy { itemView.findViewById(R.id.inputValue) as TextInputLayout }
    private val editText by lazy {
        if (hasInputValue) inputValue.editText
        else itemView.findViewById(R.id.inputEditText) as EditText
    }
    private val labelTextView by lazy { itemView.findViewById(R.id.inputLabel) as TextView }
    private val errorTextView by lazy { itemView.findViewById(R.id.inputErrorText) as TextView }
    private val errorImageView by lazy { itemView.findViewById(R.id.inputErrorImage) as ImageView }

    private fun setLabel(label: Spanned) {
        if (hasInputValue) inputValue.hint = label
        else labelTextView.text = label
    }

    private fun setErrorText(error: String?) {
        if (hasInputValue) {
            inputValue.error = error
            inputValue.isErrorEnabled = error != null
        }
        else errorTextView.text = error
    }

    private fun showErrorImage(show: Boolean) {
        if (show) errorImageView.visible(true)
        else errorImageView.invisible(true)
    }

    fun InputTextType.toAndroidInputType() = when (this) {
        InputTextType.Text -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        InputTextType.CapWords -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        InputTextType.Email -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        InputTextType.Phone -> InputType.TYPE_CLASS_PHONE
        InputTextType.Number -> InputType.TYPE_CLASS_NUMBER
        is InputTextType.Multiline -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }
}
