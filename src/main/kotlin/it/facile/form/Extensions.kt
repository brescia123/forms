package it.facile.form

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import it.facile.form.storage.FieldValue
import it.facile.form.ui.CanBeHidden
import rx.Single
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.text.DateFormat
import java.util.*
import java.util.regex.Pattern

private const val HTTP_PATTERN = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"

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

/* ---------- Log extensions ---------- */

inline fun <reified T : Any> T.logD(text: Any?) {
    Log.d(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logE(text: Any?) {
    Log.e(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logE(throwable: Throwable) {
    Log.e(T::class.java.simpleName, Log.getStackTraceString(throwable))
}

inline fun <reified T : Any> T.logI(text: Any?) {
    Log.i(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logV(text: Any?) {
    Log.v(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logW(text: Any?) {
    Log.w(T::class.java.simpleName, text.toString())
}

fun <T> T.logWW(text: Any?) where T : CanBeHidden, T : Cloneable {

}


/* ---------- Date extensions utilities ---------- */

internal val cal: Calendar by lazy { Calendar.getInstance() }

object Dates {
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


/* ---------- View extensions utilities ---------- */

fun View.visible(animate: Boolean = false,
                 animDuration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()) {
    if (visibility == View.VISIBLE) return
    if (animate) {
        alpha = 0.0f
        visibility = View.VISIBLE
        animate()
                .alpha(1.0f)
                .setDuration(animDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        visibility = View.VISIBLE
                    }
                })
    } else visibility = View.VISIBLE
}

fun View.invisible(animate: Boolean = false,
                   animDuration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()) {
    if (visibility == View.INVISIBLE) return
    if (animate) animate()
            .alpha(0.0f)
            .setDuration(animDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.INVISIBLE
                }
            })
    else visibility = View.INVISIBLE
}

fun View.gone(animate: Boolean = false,
              animDuration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()) {
    if (visibility == View.GONE) return
    if (animate) animate()
            .alpha(0.0f)
            .setDuration(animDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                }
            })
    else visibility = View.GONE
}


/* ---------- TextView extensions utilities ---------- */

fun TextView.setCompoundDrawables(left: Drawable? = null,
                                  top: Drawable? = null,
                                  right: Drawable? = null,
                                  bottom: Drawable? = null) {
    this.setCompoundDrawables(left, top, right, bottom)
}

fun String.toHtmlSpanned(): Spanned = if (VERSION.SDK_INT >= 24) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}

fun String.containsLink() =
        Pattern.compile(HTTP_PATTERN, Pattern.MULTILINE).toRegex().containsMatchIn(this)


/* ---------- Context extensions utilities ---------- */

fun Context.getColor(@ColorInt id: Int, theme: Resources.Theme? = null) = if (VERSION.SDK_INT >= 23) {
    getColor(id)
} else {
    resources.getColor(id, theme)
}

fun Context.PXtoDP(px: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics).toInt()
fun Context.DPtoPX(dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp.toFloat(), resources.displayMetrics).toInt()


/* ---------- Handler extensions utilities ---------- */

fun Handler.postDelayed(delayMillis: Long, r: () -> Unit) {
    postDelayed(r, delayMillis)
}


/* ---------- Adapter extensions utilities ---------- */

/**
 * Notifies an item change with some delay. Useful to update the adapter
 * when the RecyclerView is computing layout
 * */
fun RecyclerView.Adapter<*>.deferredNotifyItemChanged(position: Int) {
    Handler().postDelayed(50) { this.notifyItemChanged(position) }
}


/* ---------- Regex extensions utilities ---------- */

/** Returns if the CharSequence contains match of */
fun CharSequence.containsMatchOf(regex: Regex): Boolean = regex.containsMatchIn(this)

/** Returns if the CharSequence matches all the given regexes */
fun CharSequence.matchesAll(vararg regexes: Regex): Boolean {
    regexes.map { if (!matches(it)) return false }
    return true
}

/** Returns if the CharSequence matches at least one of the given regexes */
fun CharSequence.matchesAtLeastOne(vararg regexes: Regex): Boolean {
    regexes.map { if (matches(it)) return true }
    return false
}

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
fun FieldValue.asDate() = this as? FieldValue.DateValue
fun FieldValue.asText() = this as? FieldValue.Text
fun FieldValue.asBool() = this as? FieldValue.Bool
fun FieldValue.asBoolOrFalse() = (this as? FieldValue.Bool)?.bool ?: false