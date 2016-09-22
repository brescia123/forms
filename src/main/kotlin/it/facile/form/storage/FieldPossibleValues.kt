package it.facile.form.storage

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

    class RetrieveError(val errorMessage: String) : FieldPossibleValues()

    override fun equals(other: Any?): Boolean =
            if (other == null) false
            else when (this) {
                is Available -> other is Available && other.list == this.list
                is ToBeRetrieved -> false
                is RetrieveError -> other is RetrieveError && other.errorMessage == this.errorMessage
            }

    override fun hashCode(): Int = when (this) {
        is ToBeRetrieved -> possibleValuesSingle.hashCode() * 31
        is Available -> list.hashCode() * 31
        is RetrieveError -> errorMessage.hashCode() * 31
    }
}

