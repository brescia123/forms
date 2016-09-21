package it.facile.form.viewmodel

import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

sealed class FieldPossibleValues() {
    class Available(val list: List<DescribableWithKey>) : FieldPossibleValues()
    class ToBeRetrieved(val possibleValuesSingle: Single<List<DescribableWithKey>>) : FieldPossibleValues() {
        fun retrieve(): Single<List<DescribableWithKey>> = possibleValuesSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
    class RetrieveError(val errorMessage: String) :  FieldPossibleValues()
}

