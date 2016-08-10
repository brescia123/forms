package it.facile.form

import it.facile.form.viewmodel.FieldValue
import rx.Observable
import rx.subjects.PublishSubject

class FormStorage(val values: MutableMap<Int, FieldValueWithVisibility>) {
    val publishSubject: PublishSubject<Int> = PublishSubject.create()

    fun getValue(key: Int): FieldValue = values[key]?.value ?: FieldValue.Missing

    fun isHidden(key: Int): Boolean = values[key]?.hidden ?: false

    fun putValue(key: Int, value: FieldValue, hidden: Boolean = isHidden(key)) {
        values.put(key, FieldValueWithVisibility(value, hidden))
        publishSubject.onNext(key)
    }

    fun setVisibility(key: Int, hidden: Boolean) {
        val pair = values[key]
        pair?.let {
            values.put(key, FieldValueWithVisibility(it.value, hidden))
            publishSubject.onNext(key)
        }
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()

    fun notify(key: Int) = publishSubject.onNext(key)
}