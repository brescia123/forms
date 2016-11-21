package it.facile.form

import android.os.Build
import android.text.Html
import android.text.Spanned

inline fun <reified T : Any> T.logD(text: Any?) {
    android.util.Log.d(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logE(text: Any?) {
    android.util.Log.e(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logE(throwable: Throwable) {
    android.util.Log.e(T::class.java.simpleName, android.util.Log.getStackTraceString(throwable))
}

inline fun <reified T : Any> T.logI(text: Any?) {
    android.util.Log.i(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logV(text: Any?) {
    android.util.Log.v(T::class.java.simpleName, text.toString())
}

inline fun <reified T : Any> T.logW(text: Any?) {
    android.util.Log.w(T::class.java.simpleName, text.toString())
}

fun android.view.View.visible(animate: Boolean = false,
                              animDuration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()) {
    if (visibility == android.view.View.VISIBLE) return
    if (animate) {
        alpha = 0.0f
        visibility = android.view.View.VISIBLE
        animate()
                .alpha(1.0f)
                .setDuration(animDuration)
                .setListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator?) {
                        super.onAnimationEnd(animation)
                        visibility = android.view.View.VISIBLE
                    }
                })
    } else visibility = android.view.View.VISIBLE
}

fun android.view.View.invisible(animate: Boolean = false,
                                animDuration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()) {
    if (visibility == android.view.View.INVISIBLE) return
    if (animate) animate()
            .alpha(0.0f)
            .setDuration(animDuration)
            .setListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = android.view.View.INVISIBLE
                }
            })
    else visibility = android.view.View.INVISIBLE
}

fun android.view.View.gone(animate: Boolean = false,
                           animDuration: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()) {
    if (visibility == android.view.View.GONE) return
    if (animate) animate()
            .alpha(0.0f)
            .setDuration(animDuration)
            .setListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator?) {
                    super.onAnimationEnd(animation)
                    visibility = android.view.View.GONE
                }
            })
    else visibility = android.view.View.GONE
}

fun android.widget.TextView.setCompoundDrawables(left: android.graphics.drawable.Drawable? = null,
                                                 top: android.graphics.drawable.Drawable? = null,
                                                 right: android.graphics.drawable.Drawable? = null,
                                                 bottom: android.graphics.drawable.Drawable? = null) {
    this.setCompoundDrawables(left, top, right, bottom)
}

fun android.content.Context.getColor(@android.support.annotation.ColorInt id: Int, theme: android.content.res.Resources.Theme? = null) = if (android.os.Build.VERSION.SDK_INT >= 23) {
    getColor(id)
} else {
    resources.getColor(id, theme)
}

fun android.content.Context.PXtoDP(px: Float): Int = android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics).toInt()
fun android.content.Context.DPtoPX(dp: Int): Int = android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_PX, dp.toFloat(), resources.displayMetrics).toInt()
fun android.os.Handler.postDelayed(delayMillis: Long, r: () -> Unit) {
    postDelayed(r, delayMillis)
}

/**
 * Notifies an item change with some delay. Useful to update the adapter
 * when the RecyclerView is computing layout
 * */
fun android.support.v7.widget.RecyclerView.Adapter<*>.deferredNotifyItemChanged(position: Int) {
    android.os.Handler().postDelayed(50) { this.notifyItemChanged(position) }
}

fun String.toHtmlSpanned(): Spanned = if (Build.VERSION.SDK_INT >= 24) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}