package it.facile.form.storage

import it.facile.form.not
import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Object
import rx.Observable
import rx.subjects.PublishSubject
import java.util.*

class FormStorage(defaultEntries: Map<String, Entry>) {
    private val publishSubject: PublishSubject<Pair<String, Boolean>> = PublishSubject.create()
    private val possibleValuesMap = mutableMapOf<String, FieldPossibleValues>()
    val values = HashMap(defaultEntries)

    /* ---------- Reading methods ---------- */

    fun getValue(key: String): FieldValue = values[key]?.value ?: Missing

    fun isHidden(key: String): Boolean = values[key]?.hidden ?: false

    fun getPossibleValues(key: String): FieldPossibleValues? = possibleValuesMap[key]

    /* ---------- Writing methods ---------- */

    /** Set the new selected value for the given key and notify the change.
     * If at the given key the value already present is equal to the given one it does nothing. */
    fun putValue(key: String, value: FieldValue, userMade: Boolean = false) {
        if (value == values[key]?.value) return // No changes
        values.put(key, Entry(value, isHidden(key)))
        notify(key, userMade)
    }

    /** Clear the selected value for the given key and notify the change */
    fun clearValue(key: String, userMade: Boolean = false) {
        values.put(key, Entry(Missing, isHidden(key)))
        notify(key, userMade)
    }

    /** Modify the field visibility and notify the change, if no value is found at key it does nothing.
     * If at the given key the visibility is equal to the given one it does nothing. */
    fun setVisibility(key: String, hidden: Boolean) {
        if (values[key]?.hidden == hidden) return // No changes
        values.put(key, Entry(getValue(key), hidden))
        notify(key, false)
    }

    /** Put possible values for a particular key, clear the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing. */
    fun putPossibleValues(key: String, possibleValues: FieldPossibleValues) {
        if (possibleValues == possibleValuesMap[key]) return // No changes
        possibleValuesMap.put(key, possibleValues)
        clearValue(key)
    }

    /** Switch possible values for a particular key, switch the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing. */
    fun switchPossibleValues(key: String, possibleValues: FieldPossibleValues) {
        if (possibleValues == possibleValuesMap[key]) return // No changes
        possibleValuesMap.put(key, possibleValues)
        val currentValue = values[key]?.value
        if (currentValue is Object) switchValueAtKey(key, currentValue)
        notify(key, false)
    }

    /** Modify the field value and visibility and notify the change.
     * If at the given key value and visibility are equal to the given ones it does nothing. */
    fun putValueAndSetVisibility(key: String, value: FieldValue, hidden: Boolean) {
        if (value == values[key]?.value && values[key]?.hidden == hidden) return // No changes
        values.put(key, Entry(value, hidden))
        notify(key, false)
    }

    /** Clear possible values for the given key and and notify the change
     * If at the given key there are no possible values it does nothing. */
    fun clearPossibleValues(key: String) {
        if (not(possibleValuesMap.containsKey(key))) return
        possibleValuesMap.remove(key)
        clearValue(key)
    }

    fun observe(): Observable<Pair<String, Boolean>> = publishSubject.asObservable()

    private fun notify(key: String, userMade: Boolean) = publishSubject.onNext(key to userMade)

    private fun switchValueAtKey(fieldKey: String, value: Object) {
        val fieldPossibleValues = possibleValuesMap[fieldKey]
        if (fieldPossibleValues is Available)
            putValue(fieldKey, Object(fieldPossibleValues.list.filter { it.key == value.value.key }.first()))
    }
}