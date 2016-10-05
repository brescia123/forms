package it.facile.form.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.facile.form.R
import it.facile.form.logD
import it.facile.form.ui.adapters.SectionsAdapter
import it.facile.form.ui.viewmodel.FieldPath
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

    private val pageIndex by lazy { arguments.getInt(PAGE_INDEX) }
    private val sectionViewModels by lazy { activity.asSectionViewModelProviderOrThrow().getSectionViewModels(pageIndex) }
    private val sectionsAdapter by lazy { SectionsAdapter(sectionViewModels) }
    private val valueChangesSubject = PublishSubject.create<FieldPathWithValue>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sectionsAdapter
                .observeValueChanges()
                .map { FieldPath(it.first.fieldIndex, it.first.sectionIndex, pageIndex) pathTo it.second }
                .subscribe(valueChangesSubject)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_page, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        formRecyclerView.adapter = sectionsAdapter
        formRecyclerView.setHasFixedSize(true)
    }

    /**
     * Updates the page and the section containing the field at the given [FieldPath] using
     * the provided [PageViewModel].
     */
    fun updateField(path: FieldPath, pageViewModel: PageViewModel) {
        //TODO: use pageViewModel.title
        val sectionViewModel = pageViewModel.sections[path.sectionIndex]
        sectionsAdapter.updateField(path, sectionViewModel)
    }

    fun observeValueChanges(): Observable<FieldPathWithValue> = valueChangesSubject.asObservable()

    fun checkErrors() {
        formRecyclerView.clearFocus()
        sectionsAdapter.toggleErrorsVisibility()
        if (sectionsAdapter.areErrorsVisible() && sectionsAdapter.hasErrors()) {
            logD("First error position: ${sectionsAdapter.firstErrorPosition()}")
            formRecyclerView.smoothScrollToPosition(sectionsAdapter.firstErrorPosition())
        }
        if (!sectionsAdapter.hasErrors()) Snackbar.make(mainLayout, "No errors!", Snackbar.LENGTH_SHORT).show()
    }


    companion object {
        private val PAGE_INDEX = "pageIndex"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param pageIndex the page index
         * *
         * @return A new instance of fragment PageFragment.
         */
        fun newInstance(pageIndex: Int): PageFragment {
            val fragment = PageFragment()
            val args = Bundle()
            args.putInt(PAGE_INDEX, pageIndex.toInt())
            fragment.arguments = args
            return fragment
        }
    }

    fun FragmentActivity.asSectionViewModelProviderOrThrow(): SectionViewModelProvider =
            if (this is SectionViewModelProvider) {
                this
            } else {
                throw RuntimeException(this.toString()
                        + " must implement SectionViewModelProvider")
            }
}



