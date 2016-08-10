package it.facile.form.viewmodel

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class PageViewModel(val title: String,
                         val sections: List<SectionViewModel>) : ViewModel, Parcelable {
    override fun isHidden(): Boolean {
        return sections.filter { !it.isHidden() }.size == 0
    }

    constructor(source: Parcel): this(source.readString(), ArrayList<SectionViewModel>().apply{ source.readList(this, SectionViewModel::class.java.classLoader) })

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(title)
        dest?.writeList(sections)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<PageViewModel> = object : Parcelable.Creator<PageViewModel> {
            override fun createFromParcel(source: Parcel): PageViewModel = PageViewModel(source)
            override fun newArray(size: Int): Array<PageViewModel?> = arrayOfNulls(size)
        }
    }
}