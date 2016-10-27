package it.facile.form.ui

/**
 * Base Interface for all the presenters.
 *
 * @param T The type of view (which extends [View]) the presenter is controlling.
 */
interface PresenterApi<in T : View> {
    fun onAttach(view: T)
    fun onDetach()
    fun onDestroy()
}

/** Base Class for all the presenters. It takes care of implementing the attach/detach view mechanism. */
abstract class Presenter<T : View> : PresenterApi<T> {
    /** holds the view reference when it is attached. if the Presenter is not attached ot any View it is null. */
    var v: T? = null

    /**
     * This method should be called every time a View is attached to the Presenter (e.g. configuration changes)
     * or it is re-created.
     * Implementing it allows the Presenter to reset the View to the current state (e.g. one
     * network call is still alive and so the View has to display a ProgressBar).
     * (typically within onStart())
     *
     * @param view the [View] to be attached
     */
    override fun onAttach(view: T) {
        v = view
    }

    /**
     * This method should be called every time the View is detached from the Presenter (e.g. configuration changes).
     * Detaching a View does not mean that it will be destroyed (see [onDestroy]),
     * but just paused.
     * (typically within onStop())
     */
    override fun onDetach() {
        v = null
    }

    /**
     * This method should be called when the View is gonna be destroyed. Use it to cleanup Presenter
     * resources.
     * (typically within onDestroy())
     */
    override fun onDestroy() {
    }
}

/** Base Interface for all the components (Activities, Fragments..) that act as views in MVP. */
interface View {}