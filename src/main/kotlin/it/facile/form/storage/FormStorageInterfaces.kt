package it.facile.form.storage

import it.facile.form.storage.FieldPossibleValues.Available
import it.facile.form.storage.FieldPossibleValues.ToBeRetrieved
import rx.Observable

interface FormStorageApi {

    val values: Map<String, Entry>

    fun getValue(key: String): FieldValue

    fun isHidden(key: String): Boolean

    fun isDisabled(key: String): Boolean

    fun getPossibleValues(key: String): FieldPossibleValues?

    fun ping(key: String)

    /* ---------- Writing methods ---------- */

    /** Set the new selected value for the given key and notify the change.
     * If at the given key the value already present is equal to the given one it does nothing. */
    fun putValue(key: String, value: FieldValue, userMade: Boolean = false)

    fun disable(key: String, userMade: Boolean = false)

    fun enable(key: String, userMade: Boolean = false)

    /** Clear the selected value for the given key and notify the change */
    fun clearValue(key: String, userMade: Boolean = false)

    /** Modify the field visibility and notify the change, if no value is found at key it does nothing.
     * If at the given key the visibility is equal to the given one it does nothing. */
    fun setVisibility(key: String, hidden: Boolean)

    /** Put possible values for a particular key, clear the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing. */
    fun putPossibleValues(key: String, possibleValues: FieldPossibleValues)

    /** Switch possible values for a particular key, switch the associated value and notify the change
     * If at the given key the possible values already present are equal the given ones it does nothing.
     * If the old and new PossibleValues are of a different type ([Available] vs [ToBeRetrieved]) or,
     * if both [Available], if the size are different or the set of keys are different,
     * the selected value is cleared. */
    fun switchPossibleValues(key: String, possibleValues: FieldPossibleValues)

    /** Modify the field value and visibility and notify the change.
     * If at the given key value and visibility are equal to the given ones it does nothing. */
    fun putValueAndSetVisibility(key: String, value: FieldValue, hidden: Boolean)

    /** Clear possible values for the given key and and notify the change
     * If at the given key there are no possible values it does nothing. */
    fun clearPossibleValues(key: String)

    /** Emits key of values changed and if it was an user-made change */
    fun observe(): Observable<Pair<String, Boolean>>
}