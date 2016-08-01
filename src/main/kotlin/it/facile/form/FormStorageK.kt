package it.facile.form

import it.facile.form.viewmodel.FieldValueK
import rx.Observable
import rx.subjects.PublishSubject

class FormStorageK(val values: MutableMap<Int, FieldValueK>) {
    val publishSubject: PublishSubject<Int> = PublishSubject.create()

    fun getValue(key: Int) = values[key] ?: FieldValueK.Empty

    fun putValue(key: Int, value: FieldValueK) {
        values.put(key, value)
        publishSubject.onNext(key)
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()
}