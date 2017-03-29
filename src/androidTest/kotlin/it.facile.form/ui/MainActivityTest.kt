package it.facile.form.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CheckBox
import it.facile.form.R
import it.facile.form.model.models.FormModel
import it.facile.form.storage.FormStorage
import it.facile.form.ui.adapters.FieldsLayouts
import it.facile.form.ui.adapters.SectionsAdapter
import it.facile.form.ui.viewmodel.SectionViewModel
import kotlinx.android.synthetic.main.activity_main.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivityTest : AppCompatActivity() {
    private val formRecyclerView: FormRecyclerView by lazy { singlePageFormRecyclerView }
    private var sectionsAdapter: SectionsAdapter? = null


    private val formModel = TestForm.FORM_MODEL(FormStorage.empty(), Schedulers.io(), AndroidSchedulers.mainThread())


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initForm(formModel, formModel.pages[0].buildPageViewModel(formModel.storage).sections, 0)
        showErrorsCheckBox.setOnClickListener { formRecyclerView.showFormError((it as CheckBox).isChecked) }
    }

    override fun onResume() {
        super.onResume()
    }

    fun initForm(formModel: FormModel, sectionViewModels: List<SectionViewModel>, pageIndex: Int) {
        formRecyclerView.formModel = formModel
        formRecyclerView.pageIndex = pageIndex
        if (sectionsAdapter == null) {
            sectionsAdapter = SectionsAdapter(
                    sectionViewModels = sectionViewModels,
                    fieldsLayouts = FieldsLayouts())
            formRecyclerView.adapter = sectionsAdapter
        }
    }
}