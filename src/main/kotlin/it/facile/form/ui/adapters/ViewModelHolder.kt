package it.facile.form.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ViewModelHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun hide(isHidden: Boolean) {
        val param = itemView.layoutParams as RecyclerView.LayoutParams
        if (isHidden) {
            param.height = 0
        } else {
            param.height = getHeight()
        }
        itemView.layoutParams = param
    }

    abstract fun getHeight(): Int
}