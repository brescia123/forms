package it.facile.form.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import it.facile.form.ui.PageFragment
import it.facile.form.ui.viewmodel.FieldPath
import it.facile.form.ui.viewmodel.FieldViewModel
import it.facile.form.ui.viewmodel.PageViewModel
import it.facile.form.ui.viewmodel.SectionViewModel

class PagesAdapter(val pageViewModels: List<PageViewModel>,
                   fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val pageFragments: MutableMap<Int, PageFragment> = mutableMapOf()

    override fun getItem(position: Int): Fragment {
        val pageFragment = PageFragment.newInstance(position)
        pageFragments.put(position, pageFragment)
        return pageFragments[position]!!
    }

    override fun getCount(): Int = pageViewModels.size

    fun getPageFragment(position: Int) = pageFragments[position]

    /**
     * Updates the [FieldViewModel] at the given position taking care of notifying the changes when
     * appropriate. It also need the [SectionViewModel] of the section containing it to be able to draw
     * the section header correctly
     */
    fun updateField(path: FieldPath, viewModel: FieldViewModel, sectionViewModel: SectionViewModel) {
        pageFragments[path.pageIndex]?.updateField(path, viewModel, sectionViewModel)
    }
}

