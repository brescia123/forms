package it.facile.form

import java.text.DateFormat
import java.util.*

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
    fun create(year: Int = -1, month: Int = -1, day: Int = -1): Date {
        cal.time = Date()
        if (year > -1) cal.set(Calendar.YEAR, year)
        if (month > -1) cal.set(Calendar.MONTH, month - 1)
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