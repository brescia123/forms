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
import it.facile.form.ui.adapters.SectionsAdapter
import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.FieldPathSection
import it.facile.form.ui.viewmodel.PageViewModel
import it.facile.form.ui.viewmodel.SectionViewModel
import kotlinx.android.synthetic.main.fragment_page.*
import rx.Observable
import rx.subjects.PublishSubject

/**
 * A simple [Fragment] subclass.
 * Use the [PageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PageFragment : Fragment() {

    var sectionViewModels: List<SectionViewModel>? = null
    private var sectionsAdapter: SectionsAdapter? = null
    private val valueChangesSubject = PublishSubject.create<Pair<FieldPathSection, FieldValue>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_page, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        sectionViewModels?.let {
            sectionsAdapter = SectionsAdapter(it)
            sectionsAdapter?.observeValueChanges()?.subscribe(valueChangesSubject)
            formRecyclerView.adapter = sectionsAdapter
            formRecyclerView.setHasFixedSize(true)
        }
    }


    /**
     * Updates the page and the section containing the field at the given [FieldPath] using
     * the provided [PageViewModel].
     */
    fun updateField(path: FieldPath, pageViewModel: PageViewModel) {
        //TODO: use pageViewModel.title
        val sectionViewModel = pageViewModel.sections[path.sectionIndex]
        sectionsAdapter?.updateField(path, sectionViewModel)
    }

    fun observeValueChanges(): Observable<Pair<FieldPathSection, FieldValue>> = valueChangesSubject.asObservable()

    fun checkErrors() {
        sectionsAdapter?.let {
            formRecyclerView.clearFocus()
            it.toggleErrorsVisibility()
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
        fun newInstance(): PageFragment {
            return PageFragment()
        }
    }
}



