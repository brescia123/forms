package it.facile.form

import android.util.Log
import it.facile.form.viewmodel.FieldValue
import rx.Observable
import rx.subjects.PublishSubject

class FormStorage(val values: MutableMap<Int, Pair<FieldValue, Boolean>>) {
    val publishSubject: PublishSubject<Int> = PublishSubject.create()

    fun getValue(key: Int): FieldValue = values[key]?.first ?: FieldValue.Missing

    fun isHidden(key: Int): Boolean = values[key]?.second ?: true

    fun putValue(key: Int, value: FieldValue, hidden: Boolean = isHidden(key)) {
        values.put(key, value to hidden)
        publishSubject.onNext(key)
    }

    fun setVisibility(key: Int, hidden: Boolean) {
        val pair = values[key]
        pair?.let {
            values.put(key, it.first to hidden)
            publishSubject.onNext(key)
            Log.d("FormStorage", "changed visibility $key to ${if(hidden) "invisible" else "visible"}")
        }
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()

    fun notify(key: Int) = publishSubject.onNext(key)
}