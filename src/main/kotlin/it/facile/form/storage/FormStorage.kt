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

    fun isDisabled(key: String): Boolean = values[key]?.disabled ?: false

    fun getPossibleValues(key: String): FieldPossibleValues? = possibleValuesMap[key]

    fun ping(key: String) {
        notify(key, false)
    }

    /* ---------- Writing methods ---------- */

    /** Set the new selected value for the given key and notify the change.
     * If at the given key the value already present is equal to the given one it does nothing. */
    fun putValue(key: String, value: FieldValue, userMade: Boolean = false) {
        if (value == getValue(key)) return // No changes
        values.put(key, Entry(value, isHidden(key), isDisabled(key)))
        notify(key, userMade)
    }

    fun disable(key: String, userMade: Boolean = false) {
        values.put(key, Entry(getValue(key), isHidden(key), true))
        notify(key, userMade)
    }

    fun enable(key: String, userMade: Boolean = false) {
        values.put(key, Entry(getValue(key), isHidden(key), false))
        notify(key, userMade)
    }

    /** Clear the selected value for the given key and notify the change */
    fun clearValue(key: String, userMade: Boolean = false) {
        values.put(key, Entry(Missing, isHidden(key), isDisabled(key)))
        notify(key, userMade)
    }

    /** Modify the field visibility and notify the change, if no value is found at key it does nothing.
     * If at the given key the visibility is equal to the given one it does nothing. */
    fun setVisibility(key: String, hidden: Boolean) {
        if (isHidden(key) == hidden) return // No changes
        values.put(key, Entry(getValue(key), hidden, isDisabled(key)))
        notify(key, false)
    }

    /** Put possible values for a particular key, clear the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing. */
    fun putPossibleValues(key: String, possibleValues: FieldPossibleValues) {
        if (possibleValues == getPossibleValues(key)) return // No changes
        possibleValuesMap.put(key, possibleValues)
        clearValue(key)
    }

    /** Switch possible values for a particular key, switch the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing.
     * If the old and new PossibleValues are of a different type ([Available] vs [ToBeRetrieved]) or,
     * if both [Available], if the size are different or the set of keys are different,
     * the selected value is cleared. */
    fun switchPossibleValues(key: String, possibleValues: FieldPossibleValues) {
        val oldPossibleValues = getPossibleValues(key)
        if (possibleValues == oldPossibleValues) return // No changes
        possibleValuesMap.put(key, possibleValues)
        val selectedValue = values[key]?.value
        if (possibleValues.isCompatibleWith(oldPossibleValues) && selectedValue is Object) {
            switchValueAtKey(key, selectedValue)
        } else {
            clearValue(key)
        }
    }

    /** Modify the field value and visibility and notify the change.
     * If at the given key value and visibility are equal to the given ones it does nothing. */
    fun putValueAndSetVisibility(key: String, value: FieldValue, hidden: Boolean) {
        if (value == getValue(key) && hidden == isHidden(key)) return // No changes
        values.put(key, Entry(value, hidden, isDisabled(key)))
        notify(key, false)
    }

    /** Clear possible values for the given key and and notify the change
     * If at the given key there are no possible values it does nothing. */
    fun clearPossibleValues(key: String) {
        if (not(possibleValuesMap.containsKey(key))) return
        possibleValuesMap.remove(key)
        clearValue(key)
    }

    /** Emits key of values changed and if it was an user-made change */
    fun observe(): Observable<Pair<String, Boolean>> = publishSubject.asObservable()

    private fun notify(key: String, userMade: Boolean) = publishSubject.onNext(key to userMade)

    private fun switchValueAtKey(fieldKey: String, value: Object) {
        val fieldPossibleValues = possibleValuesMap[fieldKey]
        if (fieldPossibleValues is Available)
            putValue(fieldKey, Object(fieldPossibleValues.list.first { it.key == value.value.key }))
    }

    private fun FieldPossibleValues.isCompatibleWith(possibleValues: FieldPossibleValues?) =
            possibleValues is Available
                    && this is Available
                    && list.size == possibleValues.list.size
                    && list.map { it.key }.containsAll(possibleValues.list.map { it.key })
}