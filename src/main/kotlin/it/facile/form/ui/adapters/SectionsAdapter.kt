package it.facile.form.ui.adapters

import android.os.Handler
import android.support.v7.widget.RecyclerView
import it.facile.form.R
import it.facile.form.SectionedRecyclerViewAdapter
import it.facile.form.model.configuration.CustomPickerId
import it.facile.form.viewmodel.FieldPathSection
import it.facile.form.viewmodel.FieldValue
import it.facile.form.viewmodel.FieldViewModel
import it.facile.form.viewmodel.SectionViewModel
import rx.Observable

class SectionsAdapter(val sectionViewModels: List<SectionViewModel>,
                      onCustomPickerClicked: (CustomPickerId, (FieldValue) -> Unit) -> Unit)
: SectionedRecyclerViewAdapter(R.layout.form_section_header, R.layout.form_section_first_header) {
    private val fieldsAdapter: FieldsAdapter
    private val recyclerViews: MutableList<RecyclerView> = mutableListOf()

    init {
        setAwareSections(buildPositionAwareSectionViewModels())
        val fieldViewModels = sectionViewModels.fold(mutableListOf<FieldViewModel>(),
                { list, item ->
                    list.addAll(item.fields)
                    list
                })
        fieldsAdapter = FieldsAdapter(fieldViewModels, onCustomPickerClicked)
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

        val sectionIndex = sectionIndexFromFieldAbsolutePosition(absolutePosition)
        val isHidingOrShowing = viewModel.hidden != fieldsAdapter.getViewModel(absolutePosition).hidden
        val isSectionViewModelUpdated = !sectionViewModel.equals(sectionViewModels[sectionIndex!!])

        val sectionedPosition = positionToSectionedPosition(absolutePosition)
        fieldsAdapter.setFieldViewModel(absolutePosition, viewModel)
        setAwareSection(sectionViewModel.buildPositionAware(sectionIndex))

        recyclerViews.map {
            val view = it.layoutManager.findViewByPosition(sectionedPosition)
            if (!(view?.hasFocus() ?: true) || isHidingOrShowing) { // If the view has focus don't reload it
                if (it.isComputingLayout) { // Defer view update if RecyclerView is computing layout
                    deferredNotifyItemChanged(sectionedPosition)
                    if (isSectionViewModelUpdated) deferredNotifyItemChanged(awareSections.keyAt(sectionIndex))
                } else {
                    notifyItemChanged(sectionedPosition)
                    if (isSectionViewModelUpdated) notifyItemChanged(awareSections.keyAt(sectionIndex))
                }
            }
        }
    }

    fun observeValueChanges(): Observable<Pair<FieldPathSection, FieldValue>> =
            fieldsAdapter.observeValueChanges()
                    .map {
                        FieldPathSection(
                                fieldIndex = fieldIndexFromFieldAbsolutePosition(it.first)!!,
                                sectionIndex = sectionIndexFromFieldAbsolutePosition(it.first)!!
                        ) to it.second
                    }

    private fun deferredNotifyItemChanged(sectionedPosition: Int) {
        Handler().post { notifyItemChanged(sectionedPosition) }
    }

    fun buildPositionAwareSectionViewModels(): Array<PositionAwareSectionViewModel> {
        return sectionViewModels
                .mapIndexed { i, sectionViewModel -> sectionViewModel.buildPositionAware(i) }
                .toTypedArray()
    }

    fun SectionViewModel.buildPositionAware(index: Int)
            : PositionAwareSectionViewModel {
        val offset = sectionViewModels.subList(0, index).fold(0,
                { offset, section ->
                    offset + section.fields.size
                })
        return PositionAwareSectionViewModel(
                offset,
                offset + index,
                this.title,
                this.isHidden())
    }

    private fun sectionIndexFromFieldAbsolutePosition(absolutePosition: Int): Int? {
        var counter = 0
        sectionViewModels.mapIndexed { i, sectionViewModel ->
            counter += sectionViewModel.fields.size
            if (absolutePosition < counter) return i
        }
        return null
    }

    private fun fieldIndexFromFieldAbsolutePosition(absolutePosition: Int): Int? {
        var copy = absolutePosition
        for ((title, fields) in sectionViewModels) {
            for ((index, fieldViewModel) in fields.withIndex()) {
                if (copy == 0) return index
                copy--
            }
        }
        return null
    }
}
