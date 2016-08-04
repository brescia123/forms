package it.facile.form

import it.facile.form.viewmodel.FieldValue
import rx.Observable
import rx.subjects.PublishSubject

class FormStorage(val values: MutableMap<Int, Pair<FieldValue, Boolean>>) {
    val publishSubject: PublishSubject<Int> = PublishSubject.create()

    fun getValue(key: Int): FieldValue = values[key]?.first ?: FieldValue.Missing

    fun isHidden(key: Int): Boolean = values[key]?.second ?: true

    fun putValue(key: Int, value: FieldValue) {
        values.put(key, Pair(value, false))
        publishSubject.onNext(key)
    }
    fun putValue(key: Int, value: FieldValue, hidden: Boolean) {
        if (isHidden(key) == hidden) return
        values.put(key, value to hidden)
        publishSubject.onNext(key)
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()
}