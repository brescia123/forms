package it.facile.form.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import it.facile.form.model.CustomPickerId
import it.facile.form.not
import it.facile.form.storage.FieldValue
import it.facile.form.ui.ViewTypeFactory
import it.facile.form.ui.adapters.FieldViewHolders.FieldViewHolderBase
import it.facile.form.ui.viewmodel.FieldViewModel
import rx.Observable
import rx.subjects.PublishSubject

class FieldsAdapter(val viewModels: MutableList<FieldViewModel>,
                    val onCustomPickerClicked: (CustomPickerId, (FieldValue) -> Unit) -> Unit,
                    val viewTypeFactory: ViewTypeFactory,
                    val fieldViewHolderFactory: FieldViewHolderFactory)
: RecyclerView.Adapter<FieldViewHolderBase>() {

    private var errorsShouldBeVisible = false
    private val valueChangesSubject = PublishSubject.create<Pair<Int, FieldValue>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolderBase =
            fieldViewHolderFactory.createViewHolder(
                    viewType,
                    LayoutInflater.from(parent.context).inflate(viewType, parent, false),
                    valueChangesSubject,
                    onCustomPickerClicked)

    override fun onBindViewHolder(holder: FieldViewHolderBase, position: Int) =
            holder.bind(viewModels[position], position, errorsShouldBeVisible)

    override fun getItemViewType(position: Int): Int =
            viewModels[position].viewType(viewTypeFactory)

    override fun getItemCount(): Int = viewModels.size


    /* ---------- PUBLIC METHODS ---------- */

    fun getViewModel(position: Int): FieldViewModel = viewModels[position]

    fun setFieldViewModel(position: Int, fieldViewModel: FieldViewModel): FieldViewModel = viewModels.set(position, fieldViewModel)

    fun observeValueChanges(): Observable<Pair<Int, FieldValue>> = valueChangesSubject.asObservable()

    fun toggleErrorsVisibility() {
        errorsShouldBeVisible = !errorsShouldBeVisible
    }

    fun areErrorsVisible() = errorsShouldBeVisible

    /** Returns whether the adapter contains at least one visible field with an error */
    fun hasErrors() = firstErrorPosition() >= 0

    /** Return the position of the first error, -1 if no error are present */
    fun firstErrorPosition(): Int {
        for ((index, viewModel) in viewModels.withIndex()) {
            if (viewModel.hasError() and viewModel.isVisible()) {
                return index
            }
        }
        return -1
    }

    /** Return the positions of the first error, -1 if no error are present */
    fun errorPositions(): MutableList<Int> {
        val positions = mutableListOf<Int>()
        for ((index, viewModel) in viewModels.withIndex()) {
            if (viewModel.hasError() and viewModel.isVisible()) {
                positions.add(index)
            }
        }
        return positions
    }


    /* ---------- HELPER METHODS ---------- */

    private fun FieldViewModel.isVisible() = not(hidden)
    private fun FieldViewModel.hasError() = error != null
}
