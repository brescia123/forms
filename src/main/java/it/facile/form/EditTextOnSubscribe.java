package it.facile.form;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class EditTextOnSubscribe implements Observable.OnSubscribe<CharSequence> {

    private final EditText editText;

    public EditTextOnSubscribe(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void call(final Subscriber<? super CharSequence> subscriber) {

        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If the subscriber is subscribed emit the new text value
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editText.addTextChangedListener(watcher);

        // Add the a main thread safe subscription
        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                editText.removeTextChangedListener(watcher);
            }
        });

        subscriber.onNext(editText.getText());
    }
}
