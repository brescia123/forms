package it.facile.form.storage

import it.facile.form.not
import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldValue.Missing
import it.facile.form.storage.FieldValue.Object
import rx.Observable
import rx.subjects.PublishSubject
import java.util.*

class FormStorage(defaultEntries: Map<String, Entry>) : FormStorageApi {
    private val publishSubject: PublishSubject<Pair<String, Boolean>> = PublishSubject.create()
    private val possibleValuesMap = mutableMapOf<String, FieldPossibleValues>()
    override val values = HashMap(defaultEntries)

    /* ---------- Reading methods ---------- */

    override fun getValue(key: String): FieldValue = values[key]?.value ?: Missing

    override fun isHidden(key: String): Boolean = values[key]?.hidden ?: false

    override fun isDisabled(key: String): Boolean = values[key]?.disabled ?: false

    override fun getPossibleValues(key: String): FieldPossibleValues? = possibleValuesMap[key]

    override fun ping(key: String) {
        notify(key, false)
    }

    /* ---------- Writing methods ---------- */

    override fun putValue(key: String, value: FieldValue, userMade: Boolean) {
        if (value == getValue(key)) return // No changes
        values.put(key, Entry(value, isHidden(key), isDisabled(key)))
        notify(key, userMade)
    }

    override fun disable(key: String, userMade: Boolean) {
        values.put(key, Entry(getValue(key), isHidden(key), true))
        notify(key, userMade)
    }

    override fun enable(key: String, userMade: Boolean) {
        values.put(key, Entry(getValue(key), isHidden(key), false))
        notify(key, userMade)
    }

    override fun clearValue(key: String, userMade: Boolean) {
        values.put(key, Entry(Missing, isHidden(key), isDisabled(key)))
        notify(key, userMade)
    }

    override fun setVisibility(key: String, hidden: Boolean) {
        if (isHidden(key) == hidden) return // No changes
        values.put(key, Entry(getValue(key), hidden, isDisabled(key)))
        notify(key, false)
    }

    override fun putPossibleValues(key: String, possibleValues: FieldPossibleValues) {
        if (possibleValues == getPossibleValues(key)) return // No changes
        possibleValuesMap.put(key, possibleValues)
        clearValue(key)
    }

    override fun switchPossibleValues(key: String, possibleValues: FieldPossibleValues) {
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

    override fun putValueAndSetVisibility(key: String, value: FieldValue, hidden: Boolean) {
        if (value == getValue(key) && hidden == isHidden(key)) return // No changes
        values.put(key, Entry(value, hidden, isDisabled(key)))
        notify(key, false)
    }

    override fun clearPossibleValues(key: String) {
        if (not(possibleValuesMap.containsKey(key))) return
        possibleValuesMap.remove(key)
        clearValue(key)
    }

    override fun observe(): Observable<Pair<String, Boolean>> = publishSubject.asObservable()

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