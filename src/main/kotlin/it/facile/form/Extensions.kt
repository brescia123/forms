package it.facile.form

import it.facile.form.storage.FieldValue
import rx.Single
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.text.DateFormat
import java.util.*
import java.util.regex.Pattern

fun <T1, T2> Map<T1, T2>.equalMap(other: Map<T1, T2>): Boolean {
    if (size != other.size) return false
    forEach {
        if (!other.containsKey(it.key)) return false
        if (!(other[it.key]?.equals(it.value) ?: false)) return false
    }
    other.forEach {
        if (!containsKey(it.key)) return false
        if (!(this[it.key]?.equals(it.value) ?: false)) return false
    }
    return true
}


/* ---------- Date extensions utilities ---------- */

internal val cal: Calendar by lazy { Calendar.getInstance() }

object Dates {
    /** Create a date with the given year, month (Jan == 0) and day (first day of month == 1). */
    fun create(year: Int = -1, month: Int = -1, day: Int = -1): Date {
        cal.time = Date()
        if (year > -1) cal.set(Calendar.YEAR, year)
        if (month > -1) cal.set(Calendar.MONTH, month)
        if (day > -1) cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.time
    }

    fun today() = Date()
}

fun Date.year(): Int {
    cal.time = this
    return cal.get(Calendar.YEAR)
}

fun Date.month(): Int {
    cal.time = this
    return cal.get(Calendar.MONTH)
}

fun Date.dayOfMonth(): Int {
    cal.time = this
    return cal.get(Calendar.DAY_OF_MONTH)
}

fun Date.format(formatter: DateFormat): String {
    return formatter.format(this)
}

/* ---------- Regex extensions utilities ---------- */

fun String.containsLink() = Pattern.compile(
        "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
        Pattern.MULTILINE)
        .toRegex()
        .containsMatchIn(this)

/** Returns if the CharSequence contains match of */
fun CharSequence.containsMatchOf(regex: Regex): Boolean = regex.containsMatchIn(this)

/* ---------- Various extensions utilities ---------- */

/** Converts any object to a [Single] the just emit the value */
fun <T> T.toSingle(): Single<T> = Single.just(this)

/** Add the [Subscription] to a [CompositeSubscription] */
fun Subscription.addTo(compositeSubscription: CompositeSubscription) {
    compositeSubscription.add(this)
}

/** Convenient global boolean not method */
fun not(boolean: Boolean) = boolean.not()

fun FieldValue.asObject() = this as? FieldValue.Object
fun FieldValue.asObjectKey() = (this as? FieldValue.Object)?.value?.key
fun FieldValue.asObjectDescription() = (this as? FieldValue.Object)?.value?.textDescription
fun FieldValue.asDate() = this as? FieldValue.DateValue
fun FieldValue.asText() = this as? FieldValue.Text
fun FieldValue.asBool() = this as? FieldValue.Bool
fun FieldValue.asBoolOrFalse() = (this as? FieldValue.Bool)?.bool ?: false