package it.facile.form

import it.facile.form.viewmodel.FieldPossibleValues
import it.facile.form.viewmodel.FieldPossibleValues.Available
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldValue.Missing
import it.facile.form.viewmodel.FieldValue.Object
import rx.Observable
import rx.subjects.PublishSubject

class FormStorage(defaultEntries: MutableMap<Int, Entry>) {
    private val publishSubject: PublishSubject<Int> = PublishSubject.create()
    private val possibleValuesMap = mutableMapOf<Int, FieldPossibleValues>()
    val values = defaultEntries

    fun getValue(key: Int): FieldValue = values[key]?.value ?: Missing

    fun isHidden(key: Int): Boolean = values[key]?.hidden ?: false

    fun getPossibleValues(key: Int): FieldPossibleValues? = possibleValuesMap[key]

    /** Set the new selected value for the given key and notify the change.
     * If at the given key the value already present is equal to the given one it does nothing. */
    fun putValue(key: Int, value: FieldValue) {
        if (value == values[key]?.value) return // No changes
        values.put(key, Entry(value, isHidden(key)))
        notify(key)
    }

    /** Modify the field visibility and notify the change, if no value is found at key it does nothing.
     * If at the given key the visibility is equal to the given one it does nothing. */
    fun setVisibility(key: Int, hidden: Boolean) {
        if (values[key]?.hidden == hidden) return // No changes
        values.put(key, Entry(getValue(key), hidden))
        notify(key)
    }

    /** Put possible values for a particular key, clear the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing. */
    fun putPossibleValues(key: Int, possibleValues: FieldPossibleValues) {
        if (possibleValues == possibleValuesMap[key]) return // No changes
        possibleValuesMap.put(key, possibleValues)
        clearValue(key)
    }

    /** Switch possible values for a particular key, switch the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing. */
    fun switchPossibleValues(key: Int, possibleValues: FieldPossibleValues) {
        if (possibleValues == possibleValuesMap[key]) return // No changes
        possibleValuesMap.put(key, possibleValues)
        val currentValue = values[key]?.value
        if (currentValue is Object) switchValueAtKey(key, currentValue)
        notify(key)
    }

    /** Modify the field value and visibility and notify the change.
     * If at the given key value and visibility are equal to the given ones it does nothing. */
    fun putValueAndSetVisibility(key: Int, value: FieldValue, hidden: Boolean) {
        if (value == values[key]?.value && values[key]?.hidden == hidden) return // No changes
        values.put(key, Entry(value, hidden))
        notify(key)
    }

    /** Clear the selected value for the given key and notify the change */
    fun clearValue(key: Int) {
        values.put(key, Entry(Missing, isHidden(key)))
        notify(key)
    }

    /** Clear possible values for the given key and notify the change */
    fun clearPossibleValues(key: Int) {
        possibleValuesMap.remove(key)
        notify(key)
    }

    fun observe(): Observable<Int> = publishSubject.asObservable()

    private fun notify(key: Int) = publishSubject.onNext(key)

    private fun switchValueAtKey(fieldKey: Int, value: Object) {
        val fieldPossibleValues = possibleValuesMap[fieldKey]
        if (fieldPossibleValues is Available)
            putValue(fieldKey, Object(fieldPossibleValues.list[value.value.key]))
    }
}