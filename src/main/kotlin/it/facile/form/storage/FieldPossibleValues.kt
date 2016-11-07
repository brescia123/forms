package it.facile.form.storage

import rx.Single

sealed class FieldPossibleValues() {
    class Available(val list: List<DescribableWithKey>) : FieldPossibleValues() {}
    class ToBeRetrieved(val possibleValuesSingle: Single<List<DescribableWithKey>>,
                        val preselectKey: String? = null) : FieldPossibleValues() {}

    override fun equals(other: Any?): Boolean =
            if (other == null) false
            else when (this) {
                is Available -> other is Available && other.list == this.list
                is ToBeRetrieved -> false
            }

    override fun hashCode(): Int = when (this) {
        is ToBeRetrieved -> possibleValuesSingle.hashCode() * 31
        is Available -> list.hashCode() * 31
    }
}

