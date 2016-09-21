package it.facile.form

import it.facile.form.viewmodel.DescribableWithKey
import it.facile.form.viewmodel.FieldValue
import rx.Observable
import rx.subjects.PublishSubject

class FormStorage(defaultEntries: MutableMap<Int, Entry>) {
    private val publishSubject: PublishSubject<Int> = PublishSubject.create()
    private val possibleValuesMap = mutableMapOf<Int, List<DescribableWithKey>>()
    val values = defaultEntries

    fun getValue(key: Int): FieldValue = values[key]?.value ?: FieldValue.Missing

    fun isHidden(key: Int): Boolean = values[key]?.hidden ?: false

    fun getPossibleValues(key: Int): List<DescribableWithKey>? = possibleValuesMap[key]

    /** Set the new selected value for the given key, eventually modify the field visibility and notify the change */
    fun putValue(key: Int, value: FieldValue, hidden: Boolean = isHidden(key)) {
        values.put(key, Entry(value, hidden))
        publishSubject.onNext(key)
    }

    /** Modify the field visibility and notify the change, if no value is found at key it does nothing */
    fun setVisibility(key: Int, hidden: Boolean) {
        values[key]?.let { putValue(key, it.value, hidden) }
    }

    /** Add custom possible values for a particular key, clear the selected value and notify the change */
    fun putPossibleValues(key: Int, possibleValues: List<DescribableWithKey>) {
        possibleValuesMap.put(key, possibleValues)
        clearValue(key)
    }

    /** Clear the selected value for the given key and notify the change */
    fun clearValue(key: Int) {
        putValue(key, FieldValue.Missing, isHidden(key))
    }

    /** Clear possible values for the given key and notify the change */
    fun clearPossibleValues(key: Int) {
        possibleValuesMap.remove(key)
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()

    fun notify(key: Int) = publishSubject.onNext(key)
}