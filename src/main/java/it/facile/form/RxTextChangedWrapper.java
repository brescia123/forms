package it.facile.form;

import android.widget.EditText;

import rx.Observable;

public class RxTextChangedWrapper {
    public static Observable<CharSequence> wrap(EditText editText) {
        if (editText == null) return null;
        return Observable.create(new EditTextOnSubscribe(editText));
    }
}
