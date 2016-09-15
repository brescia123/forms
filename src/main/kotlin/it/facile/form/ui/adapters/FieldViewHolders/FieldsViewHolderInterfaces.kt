package it.facile.form.ui.adapters.FieldViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AnimationUtils
import it.facile.form.R
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel

/** Represent a Field that can show an error state */
interface CanShowError {
    fun animateError(itemView: View) {
        val loadAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.vibrate)
        itemView.animation = loadAnimation
        loadAnimation.start()
    }

    fun showError(itemView: View, viewModel: FieldViewModel, show: Boolean)

    fun isErrorOutdated(itemView: View, viewModel: FieldViewModel): Boolean
}

/** Represent a Field that is interactive and so can notify new values */
interface CanNotifyNewValues {
    fun notifyNewValue(position: Int, newValue: FieldValue)
}

/** Represent a Field that can be hidden by reducing its height to 0 */
interface CanBeHidden {
    fun hide(itemView: View, isHidden: Boolean) {
        val param = itemView.layoutParams as RecyclerView.LayoutParams
        if (isHidden) {
            param.height = 0
        } else {
            param.height = getHeight()
        }
        itemView.layoutParams = param
    }
    fun getHeight(): Int
}