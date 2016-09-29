package it.facile.form.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import it.facile.form.logW

class FormRecyclerView(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : this(context, attrs) {
        RecyclerView(context, attrs, defStyle)
    }

    init {
        itemAnimator = FormItemAnimator()
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
    }

    override fun setItemAnimator(animator: ItemAnimator?) {
        if (animator !is FormItemAnimator)
            logW("Using an ItemAnimator that is not a FormItemAnimator can lead to problems " +
                    "with input text fields.")
        super.setItemAnimator(animator)
    }
}

