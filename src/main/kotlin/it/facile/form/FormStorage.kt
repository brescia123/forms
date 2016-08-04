package it.facile.form

import it.facile.form.viewmodel.FieldValue
import rx.Observable
import rx.subjects.PublishSubject

class FormStorage(val values: MutableMap<Int, FieldValue>) {
    val publishSubject: PublishSubject<Int> = PublishSubject.create()

    fun getValue(key: Int) = values[key] ?: FieldValue.Missing

    fun putValue(key: Int, value: FieldValue) {
        values.put(key, value)
        publishSubject.onNext(key)
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()
}