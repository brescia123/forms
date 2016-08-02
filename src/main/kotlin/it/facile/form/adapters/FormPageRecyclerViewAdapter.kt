package it.facile.form.adapters

import android.os.Handler
import android.support.v7.widget.RecyclerView
import it.facile.form.R
import it.facile.form.SectionedRecyclerViewAdapterK
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.SectionViewModel

class FormPageRecyclerViewAdapter(sectionViewModels: List<SectionViewModel>,
                                  fieldViewModels: List<FieldViewModel>,
                                  onFieldChangedListenerList: (absolutePosition: Int, fieldValue: FieldValue) -> Unit)
: SectionedRecyclerViewAdapterK(R.layout.form_section_header, R.layout.form_section_first_header) {
    private val fieldsAdapter: FieldsRecyclerViewAdapter
    private val recyclerViews: MutableList<RecyclerView> = mutableListOf()


    init {
        setSections(sectionViewModels.toTypedArray())
        fieldsAdapter = FieldsRecyclerViewAdapter(fieldViewModels.toMutableList(), onFieldChangedListenerList)
        setAdapter(fieldsAdapter)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViews.add(recyclerView)
    }

    /**
     * Update the [FieldViewModel] at the given position taking care of notifying the changes when
     * appropriate.
     */
    fun updateField(absolutePosition: Int, viewModel: FieldViewModel) {
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
