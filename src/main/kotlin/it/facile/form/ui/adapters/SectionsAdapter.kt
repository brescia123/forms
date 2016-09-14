package it.facile.form.ui.adapters

import android.support.v7.widget.RecyclerView
import it.facile.form.R
import it.facile.form.SectionedRecyclerViewAdapter
import it.facile.form.deferredNotifyItemChanged
import it.facile.form.logD
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
        setAwareSections(sectionViewModels.buildPositionAwareList())
        val fieldViewModels = sectionViewModels.listAllFieldsViewModel()
        fieldsAdapter = FieldsAdapter(fieldViewModels, onCustomPickerClicked)
        this.adapter = fieldsAdapter
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        //TODO remove the recyclerView from the adapter
        recyclerViews.add(recyclerView)
    }

    /**
     * Updates the [FieldViewModel] at the given position taking care of notifying the changes when
     * appropriate. It also need the [SectionViewModel] of the section containing it to be able to draw
     * the section header correctly
     */
    fun updateField(absolutePosition: Int, viewModel: FieldViewModel, sectionViewModel: SectionViewModel) {

        val oldViewModel = fieldsAdapter.getViewModel(absolutePosition)
        logD("Position $absolutePosition: new fieldViewModel update request:\n" +
                "old: $oldViewModel\n" +
                "new: $viewModel\n")
        if (absolutePosition >= fieldsAdapter.itemCount) {
            logD("Not updating because position is out of bound")
            return
        } // No field at given position
        if (viewModel.equals(oldViewModel)) {
            logD("Not updating because viewModels are the same")
            return
        } // Same view model

        val sectionIndex = absolutePosition.calculateSectionIndex()
        val isSectionViewModelChanged = !sectionViewModel.equals(sectionViewModels[sectionIndex!!])
        val isViewModelChanged = !viewModel.equals(oldViewModel)

        val sectionedPosition = positionToSectionedPosition(absolutePosition)
        fieldsAdapter.setFieldViewModel(absolutePosition, viewModel)
        setAwareSection(sectionViewModel.buildPositionAware(sectionIndex))

        recyclerViews.map {
            if (isViewModelChanged or areErrorsVisible()) {
                logD("Updating...")
                if (it.isComputingLayout) { // Defer view update if RecyclerView is computing layout
                    deferredNotifyItemChanged(sectionedPosition)
                    if (isSectionViewModelChanged) deferredNotifyItemChanged(awareSections.keyAt(sectionIndex))
                } else {
                    notifyItemChanged(sectionedPosition)
                    if (isSectionViewModelChanged) notifyItemChanged(awareSections.keyAt(sectionIndex))
                }
            } else {
                logD("Not updating because viewModel is the same")
            }
        }
    }

    /**
     * Returns an [Observable] emitting a [Pair] with the [FieldPathSection] of the field that has a
     * new value and the new [FieldValue]
     * */
    fun observeValueChanges(): Observable<Pair<FieldPathSection, FieldValue>> =
            fieldsAdapter.observeValueChanges()
                    .map {
                        FieldPathSection(
                                fieldIndex = it.first.calculateFieldIndex()!!,
                                sectionIndex = it.first.calculateSectionIndex()!!
                        ) to it.second
                    }

    /* ---------- Errors related methods ---------- */

    /** Toggles the errors visibility notifying the changes to the adapter */
    fun toggleErrorsVisibility() {
        fieldsAdapter.toggleErrorsVisibility()
        fieldsAdapter.errorPositions().map { notifyItemChanged(positionToSectionedPosition(it)) }
    }

    /** Returns the sectioned position of the first occurred error, -1 if no errors are present */
    fun firstErrorPosition() = positionToSectionedPosition(fieldsAdapter.firstErrorPosition())

    /** Returns whether the adapter is currently showing errors */
    fun areErrorsVisible() = fieldsAdapter.errorsShouldBeVisible

    /** Returns whether the adapter contains at least one error */
    fun hasErrors() = fieldsAdapter.hasErrors()


    /* ---------- Helper extension functions ---------- */

    /** Builds the section view model aware of its position considering all the fields */
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

    fun List<SectionViewModel>.buildPositionAwareList() =
            mapIndexed { i, sectionViewModel -> sectionViewModel.buildPositionAware(i) }.toTypedArray()

    /** Returns a list of all the [FieldViewModel] contained in all the [SectionViewModel]*/
    fun List<SectionViewModel>.listAllFieldsViewModel() =
            fold(mutableListOf<FieldViewModel>(), { list, item ->
                list.addAll(item.fields)
                list
            })

    /** Calculates the section index for the absolute position index */
    fun Int.calculateSectionIndex(): Int? {
        var counter = 0
        sectionViewModels.mapIndexed { i, sectionViewModel ->
            counter += sectionViewModel.fields.size
            if (this < counter) return i
        }
        return null
    }

    /** Calculates the field index (the position inside the section) for the absolute position index */
    fun Int.calculateFieldIndex(): Int? {
        var copy = this
        for ((title, fields) in sectionViewModels) {
            for ((index, fieldViewModel) in fields.withIndex()) {
                if (copy == 0) return index
                copy--
            }
        }
        return null
    }
}
