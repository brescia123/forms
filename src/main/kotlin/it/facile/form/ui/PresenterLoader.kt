package it.facile.form.ui

import android.content.Context
import android.support.v4.content.Loader

/** Android Loader used to decouple [Presenter] from Activity/Fragment lifecycle. */
class PresenterLoader<T : BasePresenter<*>>(context: Context, private val factory: () -> T) : Loader<T>(context) {
    private var presenter: T? = null

    override fun onStartLoading() {
        if (presenter != null)
            deliverResult(presenter as T)
        else
            forceLoad()
    }

    override fun onForceLoad() {
        presenter = factory()
        deliverResult(presenter as T)
    }

    override fun onReset() {
        presenter?.onDestroy()
        presenter = null
    }
}