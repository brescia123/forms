package it.facile.form.adapters

import android.os.Handler
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import it.facile.form.viewmodel.FieldValueK
import it.facile.form.viewmodel.FieldViewModelK
import it.facile.form.viewmodel.SectionViewModelK

class FormPageRecyclerViewAdapterK(@LayoutRes sectionLayout: Int,
                                   sectionViewModels: List<SectionViewModelK>,
                                   fieldViewModels: List<FieldViewModelK>,
                                   onFieldChangedListenerlist: (absolutePosition: Int, fieldValue: FieldValueK) -> Unit) : it.facile.form.SectionedRecyclerViewAdapterK(sectionLayout) {
    private val fieldsAdapter: FieldsRecyclerViewAdapterK
    private val recyclerViews: MutableList<RecyclerView> = mutableListOf()


    init {
        setSections(sectionViewModels.toTypedArray())
        fieldsAdapter = FieldsRecyclerViewAdapterK(fieldViewModels.toMutableList(), onFieldChangedListenerlist)
        setAdapter(fieldsAdapter)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViews.add(recyclerView)
    }

    /**
     * Update the [FieldViewModelK] at the given position taking care of notifying the changes when
     * appropriate.
     */
    fun updateField(absolutePosition: Int, viewModel: FieldViewModelK) {
        if (absolutePosition >= fieldsAdapter.itemCount) return // No field at given position
        if (viewModel.equals(fieldsAdapter.getViewModel(absolutePosition))) return // Same view model

        val sectionedPosition = positionToSectionedPosition(absolutePosition)
        fieldsAdapter.setFieldViewModel(absolutePosition, viewModel)

        recyclerViews.map {
            val view = it.layoutManager.findViewByPosition(sectionedPosition)
            if (!(view?.hasFocus() ?: true)) { // If the view has focus don't reload it
                if (it.isComputingLayout) // Defer view update if RecyclerView is computing layout
                    deferredNotifyItemChanged(sectionedPosition)
                else
                    notifyItemChanged(sectionedPosition)
            }
        }
    }

    private fun deferredNotifyItemChanged(sectionedPosition: Int) {
        Handler().post { notifyItemChanged(sectionedPosition) }
    }
}
