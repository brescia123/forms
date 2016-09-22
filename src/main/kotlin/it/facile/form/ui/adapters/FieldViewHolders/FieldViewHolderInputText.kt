package it.facile.form.ui.adapters.FieldViewHolders

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import it.facile.form.R
import it.facile.form.RxTextChangedWrapper
import it.facile.form.logD
import it.facile.form.logE
import it.facile.form.storage.FieldValue
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
        val editText = itemView.inputValue.editText
        itemView.inputValue.hint = viewModel.label
        if (isTextChanged(viewModel, editText)) editText?.setText(style.textDescription)
        editText?.setOnFocusChangeListener(null)
        editText?.setOnKeyListener(null)
        subscription?.unsubscribe()
        when (style) {
            is FieldViewModelStyle.InputText -> {
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
                        { throwable -> logE(throwable.message) })
            }
        }
    }

    override fun getHeight(): Int {
        return itemView.resources.getDimension(R.dimen.field_height_input_text).toInt()
    }

    override fun notifyNewValue(position: Int, newValue: FieldValue) {
        valueChangesSubject.onNext(position to newValue)
    }

    override fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean) {
        itemView.inputValue.error = if (show) viewModel.error else null
    }

    override fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean =
            viewModel.error != itemView.inputValue.error.toString()

    private fun isTextChanged(viewModel: FieldViewModel, editText: EditText?) =
            !viewModel.style.textDescription.equals(editText?.text.toString())

}