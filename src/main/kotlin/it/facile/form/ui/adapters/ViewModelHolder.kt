package it.facile.form.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import it.facile.form.viewmodel.ViewModel

abstract class ViewModelHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun hide(viewModel: ViewModel) {
        val param = itemView.layoutParams as RecyclerView.LayoutParams
        if (viewModel.isHidden()) {
            param.height = 0
        } else {
            param.height = getHeight()
        }
        itemView.layoutParams = param
    }

    abstract fun getHeight(): Int
}