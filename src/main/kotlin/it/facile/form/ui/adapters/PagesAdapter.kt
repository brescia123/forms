package it.facile.form.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import it.facile.form.storage.FieldValue
import it.facile.form.ui.FieldPathWithValue
import it.facile.form.ui.PageFragment
import it.facile.form.ui.pathTo
import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.PageViewModel
import rx.Observable
import rx.subjects.PublishSubject

class PagesAdapter(val pageViewModels: List<PageViewModel>,
                   val fieldsLayouts: FieldsLayouts = FieldsLayouts(),
                   val customPickerActions: Map<String, ((FieldValue) -> Unit) -> Unit> = emptyMap(),
                   val customBehaviours: Map<String, () -> Unit> = emptyMap(),
                   fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val pageFragments: MutableMap<Int, PageFragment> = mutableMapOf()
    private val valueChangesSubject = PublishSubject.create<FieldPathWithValue>()

    override fun getItem(position: Int): Fragment {
        val pageFragment = PageFragment.newInstance()
        pageFragments.put(position, pageFragment)
        pageFragment.pageViewModel = pageViewModels[position]
        pageFragment.fieldsLayouts = fieldsLayouts
        pageFragment.customPickerActions = customPickerActions
        pageFragment.customBehaviours = customBehaviours
        pageFragment
                .observeValueChanges()
                .map { FieldPath(it.first.fieldIndex, it.first.sectionIndex, position) pathTo it.second }
                .subscribe(valueChangesSubject)
        return pageFragment
    }

    override fun getCount(): Int = pageViewModels.size


    fun getPageFragment(position: Int) = pageFragments[position]

    /**
     * Updates the [PageFragment] at the that contains the fields at the given [FieldPath] using the
     * provided [PageViewModel].
     */
    fun updatePage(path: FieldPath, pageViewModel: PageViewModel) {
        pageFragments[path.pageIndex]?.updatePage(path, pageViewModel)
    }

    fun observeValueChanges(): Observable<FieldPathWithValue> = valueChangesSubject.asObservable()
}

