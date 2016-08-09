package it.facile.form.ui.adapters

import android.os.Handler
import android.support.v7.widget.RecyclerView
import it.facile.form.R
import it.facile.form.SectionedRecyclerViewAdapter
import it.facile.form.model.configuration.CustomPickerId
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.SectionViewModel
import rx.Observable

class SectionsAdapter(sectionViewModels: List<SectionViewModel>,
                      fieldViewModels: List<FieldViewModel>,
                      onCustomPickerClicked: (CustomPickerId, (FieldValue) -> Unit) -> Unit)
: SectionedRecyclerViewAdapter(R.layout.form_section_header, R.layout.form_section_first_header) {
    private val fieldsAdapter: FieldsAdapter
    private val recyclerViews: MutableList<RecyclerView> = mutableListOf()

    init {
        setSections(sectionViewModels.toTypedArray())
        fieldsAdapter = FieldsAdapter(fieldViewModels.toMutableList(), onCustomPickerClicked)
        adapter = fieldsAdapter
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViews.add(recyclerView)
    }

    /**
     * Update the [FieldViewModel] at the given position taking care of notifying the changes when
     * appropriate.
     */
    fun updateField(absolutePosition: Int, viewModel: FieldViewModel, sectionViewModel: SectionViewModel) {
        if (absolutePosition >= fieldsAdapter.itemCount) return // No field at given position
        if (viewModel.equals(fieldsAdapter.getViewModel(absolutePosition))) return // Same view model

        val isHidingOrShowing = viewModel.hidden != fieldsAdapter.getViewModel(absolutePosition).hidden
        val isSectionViewModelUpdated = !sectionViewModel.equals(sections[sectionViewModel.sectionedPosition])

        val sectionedPosition = positionToSectionedPosition(absolutePosition)
        fieldsAdapter.setFieldViewModel(absolutePosition, viewModel)
        setSection(sectionViewModel)

        recyclerViews.map {
            val view = it.layoutManager.findViewByPosition(sectionedPosition)
            if (!(view?.hasFocus() ?: true) || isHidingOrShowing) { // If the view has focus don't reload it
                if (it.isComputingLayout) { // Defer view update if RecyclerView is computing layout
                    deferredNotifyItemChanged(sectionedPosition)
                    if (isSectionViewModelUpdated) deferredNotifyItemChanged(sectionViewModel.sectionedPosition)
                } else {
                    notifyItemChanged(sectionedPosition)
                    if (isSectionViewModelUpdated) notifyItemChanged(sectionViewModel.sectionedPosition)
                }
            }
        }
    }
    fun observeValueChanges(): Observable<Pair<Int, FieldValue>> = fieldsAdapter.observeValueChanges()

    private fun deferredNotifyItemChanged(sectionedPosition: Int) {
        Handler().post { notifyItemChanged(sectionedPosition) }
    }
}
