package it.facile.form.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.facile.form.R
import it.facile.form.logD
import it.facile.form.storage.FieldValue
import it.facile.form.ui.adapters.FieldsLayouts
import it.facile.form.ui.adapters.SectionsAdapter
import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.FieldPathSection
import it.facile.form.ui.viewmodel.PageViewModel
import kotlinx.android.synthetic.main.fragment_page.*
import rx.Observable
import rx.subjects.PublishSubject

/**
 * A simple [Fragment] subclass.
 * Use the [PageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PageFragment : Fragment() {

    lateinit var pageViewModel: PageViewModel
    var fieldsLayouts: FieldsLayouts = FieldsLayouts()
    var customPickerActions: Map<String, ((FieldValue) -> Unit) -> Unit>? = null
    var customBehaviours: Map<String, () -> Unit>? = null
    private var sectionsAdapter: SectionsAdapter? = null
    private val valueChangesSubject = PublishSubject.create<Pair<FieldPathSection, FieldValue>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_page, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        sectionsAdapter = SectionsAdapter(
                sectionViewModels = pageViewModel.sections,
                fieldsLayouts = fieldsLayouts,
                customBehaviours = customBehaviours ?: emptyMap(),
                customPickerActions = customPickerActions ?: emptyMap())
        sectionsAdapter?.observeValueChanges()?.subscribe(valueChangesSubject)
        formRecyclerView.adapter = sectionsAdapter
        formRecyclerView.setHasFixedSize(true)
    }

    /**
     * Updates the page and the section containing the field at the given [FieldPath] using
     * the provided [PageViewModel].
     */
    fun updatePage(path: FieldPath, newPageViewModel: PageViewModel) {
        //TODO: use pageViewModel.title
        pageViewModel = newPageViewModel

        //update section
        val sectionViewModel = pageViewModel.sections[path.sectionIndex]
        sectionsAdapter?.updateSection(path, sectionViewModel)

        //update field
        val fieldViewModel = pageViewModel.sections[path.sectionIndex].fields[path.fieldIndex]
        sectionsAdapter?.updateField(path, fieldViewModel)
    }

    fun observeValueChanges(): Observable<Pair<FieldPathSection, FieldValue>> = valueChangesSubject.asObservable()


    fun checkErrors(show: Boolean) {
        sectionsAdapter?.let {
            formRecyclerView.clearFocus()
            it.showErrors(show)
            if (it.areErrorsVisible() && it.hasErrors()) {
                logD("First error position: ${it.firstErrorPosition()}")
                formRecyclerView.smoothScrollToPosition(it.firstErrorPosition())
            }
            if (!it.hasErrors()) Snackbar.make(mainLayout, "No errors!", Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment PageFragment.
         */
        fun newInstance(): PageFragment = PageFragment()

    }
}



