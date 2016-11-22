package it.facile.form.ui.utils

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import it.facile.form.ui.adapters.FieldViewHolders.FieldViewHolderInputText

open class FormDefaultItemAnimator : DefaultItemAnimator() {
    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder is FieldViewHolderInputText
    }
}