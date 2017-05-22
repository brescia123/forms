package it.facile.form.ui.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import rx.Observable
import rx.Subscriber
import rx.android.MainThreadSubscription

class EditTextOnSubscribe
(private val editText: EditText, private val initialVal: Boolean) : Observable.OnSubscribe<Pair<TextWatcher, CharSequence>> {

    override fun call(subscriber: Subscriber<in Pair<TextWatcher, CharSequence>>) {

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (subscriber.isUnsubscribed) return

                subscriber.onNext(Pair(this, editText.text))
            }

            override fun afterTextChanged(s: Editable) {

            }
        }
        editText.addTextChangedListener(watcher)

        // Add the a main thread safe subscription
        subscriber.add(
                object : MainThreadSubscription() {
                    override fun onUnsubscribe() {
                        editText.removeTextChangedListener(watcher)
                    }
                })

        if (initialVal) subscriber.onNext(Pair(watcher, editText.text))
    }
}
