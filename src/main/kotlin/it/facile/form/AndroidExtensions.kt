package it.facile.form

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import it.facile.form.ui.utils.EditTextOnSubscribe
import rx.Observable


/* ---------- Log ---------- */

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


/* ---------- View ---------- */

fun View.visible(animate: Boolean = false,
                 animDuration: Long = 200) {
    if (visibility == View.VISIBLE) return
    if (animate) {
        alpha = 0.0f
        visibility = View.VISIBLE
        animate()
                .alpha(1.0f)
                .setDuration(animDuration)
                .setListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator?) {
                        super.onAnimationEnd(animation)
                        visibility = View.VISIBLE
                    }
                })
    } else visibility = View.VISIBLE
}

fun View.invisible(animate: Boolean = false,
                   animDuration: Long = 200) {
    if (visibility == View.INVISIBLE) return
    if (animate) animate()
            .alpha(0.0f)
            .setDuration(animDuration)
            .setListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.INVISIBLE
                }
            })
    else visibility = View.INVISIBLE
}

fun View.gone(animate: Boolean = false,
              animDuration: Long = 200) {
    if (visibility == View.GONE) return
    if (animate) animate()
            .alpha(0.0f)
            .setDuration(animDuration)
            .setListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                }
            })
    else visibility = View.GONE
}

fun EditText.wrap(initialVal: Boolean = true): Observable<CharSequence> = Observable.create(EditTextOnSubscribe(this, initialVal))

fun android.widget.TextView.setCompoundDrawables(left: Drawable? = null,
                                                 top: Drawable? = null,
                                                 right: Drawable? = null,
                                                 bottom: Drawable? = null) {
    this.setCompoundDrawables(left, top, right, bottom)
}

fun Context.getColor(@android.support.annotation.ColorInt id: Int, theme: Resources.Theme? = null) = if (Build.VERSION.SDK_INT >= 23) {
    resources.getColor(id, theme)
} else {
    resources.getColor(id)
}

fun Context.PXtoDP(px: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics).toInt()
fun Context.DPtoPX(dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp.toFloat(), resources.displayMetrics).toInt()
fun Handler.postDelayed(delayMillis: Long, r: () -> Unit) {
    postDelayed(r, delayMillis)
}

/**
 * Notifies an item change with some delay. Useful to update the adapter
 * when the RecyclerView is computing layout
 * */
fun RecyclerView.Adapter<*>.deferredNotifyItemChanged(position: Int) {
    android.os.Handler().postDelayed(50) { this.notifyItemChanged(position) }
}

fun String.toHtmlSpanned(): Spanned = if (Build.VERSION.SDK_INT >= 24) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}